package com.djccnt15.northwind.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler
    ) throws Exception {
        var requestURI = request.getRequestURI();
        
        if (handler instanceof HandlerMethod) {
            var hm = (HandlerMethod) handler;
        }
        
        log.info("REQUEST [{}][{}][{}]", request.getDispatcherType(), requestURI, handler);
        return true;
    }
    
    // @Override
    // public void postHandle(
    //     HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView
    // ) throws Exception {
    //     log.info("postHandle [{}]", modelAndView);
    // }
    
    @Override
    public void afterCompletion(
        HttpServletRequest request, HttpServletResponse response, Object handler, Exception e
    ) throws Exception {
        var requestURI = request.getRequestURI();
        log.info("RESPONSE [{}][{}]", requestURI, handler);
        
        if (e != null) {
            log.error("afterCompletion error", e);
        }
    }
}
