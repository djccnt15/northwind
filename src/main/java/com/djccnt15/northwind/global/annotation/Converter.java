package com.djccnt15.northwind.global.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 데이터 변환을 수행하는 클래스임을 나타내는 어노테이션.
 * <p>이 어노테이션이 붙은 클래스는 {@code @Component}로 등록되어 스프링 빈으로 관리된다.
 * <p>JPA 엔티티와 DTO 간의 변환과 같이 데이터 객체의 변환을 수행하는 컴포넌트를 구분하기 위해 사용한다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component  // register this class and implements as a spring bean
public @interface Converter {
    
    @AliasFor(annotation = Component.class)
    String value() default "";
}
