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
    
    // `@Business`는 `@Service`를 메타 애노테이션으로 합성하지만, AspectJ의 `@within`은
    // 리플렉션상 직접 붙어있는 애노테이션만 매칭하고 메타 애노테이션까지는 따라가지 않음
    // (Spring의 AnnotatedElementUtils 기반 합성 애노테이션 해석과는 다른 메커니즘)
    // 따라서 `@within(Service)`만으로는 `@Business` 클래스를 잡을 수 없어 두 조건이 모두 필요
    @Pointcut("""
        @within(com.djccnt15.northwind.global.annotation.Business)
        || @within(org.springframework.stereotype.Service)
        """)
    public void businessLayer() {}
    
    @Pointcut("execution(* com.djccnt15.northwind.db.repository..*(..))")
    public void persistentLayer() {}
}
