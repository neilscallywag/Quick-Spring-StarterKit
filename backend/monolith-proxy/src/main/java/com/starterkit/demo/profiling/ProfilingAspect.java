package com.starterkit.demo.profiling;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;



@Aspect
@Component
@Slf4j
public class ProfilingAspect {


    @Around("execution(* com.starterkit.demo.service..*(..))") // Adjust the package path as needed
    public Object profileAllMethods(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        log.error("{} executed in {}ms", pjp.getSignature(), elapsedTime);
        return output;
    }
}
