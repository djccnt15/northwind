package com.djccnt15.northwind.global.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopPointcut {
    
    @Pointcut("""
        execution(* com.djccnt15.northwind.domain..*(..))
        || execution(* com.djccnt15.northwind.global.config.security..*(..))
        """)
    public void applicationTarget() {}
    
    @Pointcut("""
        @within(com.djccnt15.northwind.global.annotation.Business)
        || @within(org.springframework.stereotype.Service)
        """)
    public void businessLayer() {}
    
    @Pointcut("!@within(com.djccnt15.northwind.global.annotation.Converter)")
    public void excludeConverter() {}
    
    @Pointcut("execution(* com.djccnt15.northwind.db.repository..*(..))")
    public void persistentLayer() {}
}
