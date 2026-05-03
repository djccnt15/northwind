package com.djccnt15.northwind.annotation;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "dev")  // only run this test when "dev" profile is active
public @interface DevTest {
    
    String value() default "";
}
