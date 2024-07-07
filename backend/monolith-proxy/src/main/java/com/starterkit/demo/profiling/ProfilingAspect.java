package com.starterkit.demo.profiling;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.togglz.core.manager.FeatureManager;
import com.starterkit.demo.features.FeatureToggle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ProfilingAspect {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MeterRegistry meterRegistry;
    private final FeatureManager featureManager;

    public ProfilingAspect(MeterRegistry meterRegistry, FeatureManager featureManager) {
        this.meterRegistry = meterRegistry;
        this.featureManager = featureManager;
    }

    @Around("execution(* com.starterkit.demo.service..*(..))")
    public Object profileAllMethods(ProceedingJoinPoint pjp) throws Throwable {
        if (featureManager.isActive(FeatureToggle.ENABLE_PROFILING)) {
            long start = System.currentTimeMillis();
            Object output = pjp.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            executorService.submit(() -> {
                log.error("{} executed in {}ms", pjp.getSignature(), elapsedTime);
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

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }
}
