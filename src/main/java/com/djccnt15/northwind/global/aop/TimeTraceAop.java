package com.djccnt15.northwind.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class TimeTraceAop {
    
    @Around("""
        AopPointcut.applicationTarget()
        && AopPointcut.businessLayer()
        && AopPointcut.excludeConverter()
        """)
    public Object executeBusiness(ProceedingJoinPoint joinPoint) throws Throwable {
        // log.info("START: {}", joinPoint);
        var stopWatch = new StopWatch();
        try {
            stopWatch.start();
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            // log.info("END: {} {}ms", joinPoint, timeMs);
            log.info("{}:  {}ms", joinPoint.getSignature(), stopWatch.getTotalTimeMillis());
        }
    }
    
    @Around("AopPointcut.persistentLayer()")
    public Object executeSql(ProceedingJoinPoint joinPoint) throws Throwable {
        // log.info("START: {}", joinPoint);
        var stopWatch = new StopWatch();
        try {
            stopWatch.start();
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            // log.info("END: {} {}ms", joinPoint, timeMs);
            log.info("{}:  {}ms", joinPoint.getSignature().toShortString(), stopWatch.getTotalTimeMillis());
        }
    }
}
