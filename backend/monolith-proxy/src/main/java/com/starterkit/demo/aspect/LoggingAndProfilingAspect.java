package com.starterkit.demo.aspect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAndProfilingAspect {

    private static final String LOGGING_SERVICE_URL = "http://logging:5001/logs";
    private static final String LOG_ID = "log-id";
    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MeterRegistry meterRegistry;

    @Autowired
    public LoggingAndProfilingAspect(RestTemplate restTemplate, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Pointcut("within(com.starterkit.demo.controller..*) || within(com.starterkit.demo.service..*)")
    public void applicationMethods() {}

    @Before("applicationMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String logId = MDC.get(LOG_ID);
        String logMessage = String.format("[%s] Executing: %s", logId, joinPoint.getSignature());
        sendLog(logMessage);
    }

    @AfterReturning(pointcut = "applicationMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String logId = MDC.get(LOG_ID);
        String logMessage =
                String.format(
                        "[%s] Completed: %s with result = %s",
                        logId, joinPoint.getSignature(), result);
        sendLog(logMessage);
    }

    @Around("applicationMethods()")
    public Object profileAndLogMethods(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;

        executorService.submit(
                () -> {
                    String logId = MDC.get(LOG_ID);
                    log.info("[{}] {} executed in {}ms", logId, pjp.getSignature(), elapsedTime);
                    Timer.builder("method.execution.time")
                            .tag("method", pjp.getSignature().toShortString())
                            .register(meterRegistry)
                            .record(elapsedTime, TimeUnit.MILLISECONDS);
                });

        return output;
    }

    @Async
    public void sendLog(String logMessage) {
        LogEntry logEntry = new LogEntry(logMessage);
        restTemplate.postForObject(LOGGING_SERVICE_URL, logEntry, Void.class);
    }

    public static class LogEntry {
        private String message;

        public LogEntry(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    // Entity transaction log handling

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommitEvent(final TransactionEvent event) {
        log.info("[{}] {} {}", event.operation().getName(), event.entityName(), event.entityId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollbackEvent(final TransactionEvent event) {
        log.info(
                "[{}] {} {} rollback.",
                event.operation().getName(),
                event.entityName(),
                event.entityId());
    }

    public static record TransactionEvent(TransactionType operation, String entityName, String entityId) {}

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum TransactionType {
        CREATE("created"),
        UPDATE("updated"),
        DELETE("deleted");

        private final String name;
    }
}
