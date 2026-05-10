package com.djccnt15.northwind.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeTraceAop {
    
    @Around("""
        (
            execution(* com.djccnt15.northwind.domain..*(..))
            || execution(* com.djccnt15.northwind.config.security..*(..))
        )
        && (
            @within(com.djccnt15.northwind.global.annotation.Business)
            || @within(org.springframework.stereotype.Service)
        )
        && !@within(com.djccnt15.northwind.global.annotation.Converter)
        """)
    public Object executeBusiness(ProceedingJoinPoint joinPoint) throws Throwable {
        // log.info("START: {}", joinPoint);
        var start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            var end = System.currentTimeMillis();
            var timeMs = end - start;
            // log.info("END: {} {}ms", joinPoint, timeMs);
            log.info("{}:  {}ms", joinPoint, timeMs);
        }
    }
    
    @Around("execution(* com.djccnt15.northwind.db.repository..*(..))")
    public Object executeSql(ProceedingJoinPoint joinPoint) throws Throwable {
        // log.info("START: {}", joinPoint);
        var start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            var end = System.currentTimeMillis();
            var timeMs = end - start;
            // log.info("END: {} {}ms", joinPoint, timeMs);
            log.info("{}:  {}ms", joinPoint, timeMs);
        }
    }
}
