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
            .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**")  // Swagger UI 및 API 문서 경로 제외
            .excludePathPatterns("/index.html", "/favicon.ico", "/static/**", "/assets/**")  // SPA 필터에서 이미 제외한 경로들
            .excludePathPatterns("/css/**", "/*.ico")  // 정적 자원 경로 추가 제외
        ;
    }
}
