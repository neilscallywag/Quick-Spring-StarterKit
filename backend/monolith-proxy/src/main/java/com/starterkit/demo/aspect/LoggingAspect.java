package com.starterkit.demo.aspect;

import com.starterkit.demo.util.LogUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final LogUtil logUtil;

    public LoggingAspect(LogUtil logUtil) {
        this.logUtil = logUtil;
    }

    @Pointcut("within(com.starterkit.demo.controller..*) || within(com.starterkit.demo.service..*)")
    public void applicationMethods() {}

    @Before("applicationMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String logId = logUtil.getLogId();
        String logMessage = String.format("[%s] Executing: %s", logId, joinPoint.getSignature());
        logUtil.sendLog(logMessage);
    }

    @AfterReturning(pointcut = "applicationMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String logId = logUtil.getLogId();
        String logMessage = String.format("[%s] Completed: %s with result = %s", logId, joinPoint.getSignature(), result);
        logUtil.sendLog(logMessage);
    }
}
