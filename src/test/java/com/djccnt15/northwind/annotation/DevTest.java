package com.djccnt15.northwind.annotation;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>only run this test when "dev" profile is active</p>
 * `loadContext = true` is required to load the application context and evaluate the expression against it
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'dev'}", loadContext = true)
@ActiveProfiles("dev")
public @interface DevTest {
    
    String value() default "";
}
