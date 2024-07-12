package com.starterkit.demo.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PreDestroy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ProfilingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ProfilingAspect.class);

    private final MeterRegistry meterRegistry;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ProfilingAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Pointcut("within(com.starterkit.demo.controller..*) || within(com.starterkit.demo.service..*)")
    public void applicationMethods() {}

    @Around("applicationMethods()")
    public Object profileMethods(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();

        logger.info("Started profiling: {}", methodName);

        Object output = pjp.proceed();

        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("Finished profiling: {}. Execution time: {} ms", methodName, elapsedTime);

        executorService.submit(() -> 
            Timer.builder("method.execution.time")
                    .tag("method", methodName)
                    .register(meterRegistry)
                    .record(elapsedTime, TimeUnit.MILLISECONDS)
        );

        return output;
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }
}
