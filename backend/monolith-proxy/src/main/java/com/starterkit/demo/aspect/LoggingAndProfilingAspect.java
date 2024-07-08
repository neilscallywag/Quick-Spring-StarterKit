package com.starterkit.demo.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.manager.FeatureManager;
import com.starterkit.demo.features.FeatureToggle;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class LoggingAndProfilingAspect {

    private static final String LOGGING_SERVICE_URL = "http://logging:5001/logs";
    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MeterRegistry meterRegistry;
    private final FeatureManager featureManager;

    @Autowired
    public LoggingAndProfilingAspect(RestTemplate restTemplate, MeterRegistry meterRegistry, FeatureManager featureManager) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        this.featureManager = featureManager;
        log.info("ENABLE_LOGGING: {}", featureManager.isActive(FeatureToggle.ENABLE_LOGGING));
        log.info("ENABLE_PROFILING: {}", featureManager.isActive(FeatureToggle.ENABLE_PROFILING));
        log.info("SEND_PROFILING_DATA_TO_PROMETHEUS: {}", featureManager.isActive(FeatureToggle.SEND_PROFILING_DATA_TO_PROMETHEUS));
    }

    @Pointcut("within(com.starterkit.demo.controller..*) || within(com.starterkit.demo.service..*)")
    public void applicationMethods() {}

    @Before("applicationMethods()")
    public void logBefore(JoinPoint joinPoint) {
        if (featureManager.isActive(FeatureToggle.ENABLE_LOGGING)) {
            String logId = MDC.get("log-id");
            String logMessage = String.format("[%s] Executing: %s", logId, joinPoint.getSignature());
            sendLog(logMessage);
        }
    }

    @AfterReturning(pointcut = "applicationMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (featureManager.isActive(FeatureToggle.ENABLE_LOGGING)) {
            String logId = MDC.get("log-id");
            String logMessage = String.format("[%s] Completed: %s with result = %s", logId, joinPoint.getSignature(), result);
            sendLog(logMessage);
        }
    }

    @Around("applicationMethods()")
    public Object profileAndLogMethods(ProceedingJoinPoint pjp) throws Throwable {
        if (featureManager.isActive(FeatureToggle.ENABLE_PROFILING)) {
            long start = System.currentTimeMillis();
            Object output = pjp.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            executorService.submit(() -> {
                String logId = MDC.get("log-id");
                log.error("[{}] {} executed in {}ms", logId, pjp.getSignature(), elapsedTime);
                if (featureManager.isActive(FeatureToggle.SEND_PROFILING_DATA_TO_PROMETHEUS)) {
                    Timer.builder("method.execution.time")
                         .tag("method", pjp.getSignature().toShortString())
                         .register(meterRegistry)
                         .record(elapsedTime, TimeUnit.MILLISECONDS);
                }
            });

            return output;
        } else {
            return pjp.proceed();
        }
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
}
