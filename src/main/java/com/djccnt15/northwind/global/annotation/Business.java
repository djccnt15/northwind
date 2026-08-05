package com.djccnt15.northwind.global.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 비즈니스 프로세스를 관리하는 컴포넌트임을 나타내는 어노테이션.
 * <p>이 어노테이션이 붙은 클래스는 {@code @Service}로 등록되어 스프링 빈으로 관리된다.
 * <p>비즈니스 프로세스를 관리하는 컴포넌트를 구분하기 위해 사용되며, 실제 비즈니스 로직을 구현하는 컴포넌트는 {@code @Service}를 사용한다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service  // register this class and implements as a spring bean
public @interface Business {
    
    @AliasFor(annotation = Service.class)
    String value() default "";
}
