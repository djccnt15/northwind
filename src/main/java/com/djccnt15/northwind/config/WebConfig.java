package com.djccnt15.northwind.config;

import com.djccnt15.northwind.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.lang.Integer.MIN_VALUE;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(new LogInterceptor())
            .order(MIN_VALUE)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/css/**", "/*.ico",
                "/error", "/error-page/**"  // 에러 발생 -> 에러 페이지 호출 시의 인터셉터 중복 호출 제거
            )
        ;
    }
}
