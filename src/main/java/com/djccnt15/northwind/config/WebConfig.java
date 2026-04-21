package com.djccnt15.northwind.config;

import com.djccnt15.northwind.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.djccnt15.northwind.constants.RouteConst.*;
import static java.lang.Integer.MIN_VALUE;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(new LogInterceptor())
            .order(MIN_VALUE)
            .addPathPatterns("/**")
            .excludePathPatterns(PUBLIC_PATHS)
            .excludePathPatterns(SESSION_CHECK_PATHS)
            .excludePathPatterns(SWAGGER_PATHS)
        ;
    }
}
