package com.djccnt15.northwind.filter;

import com.djccnt15.northwind.annotation.AppFilter;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import java.io.IOException;

import static java.lang.Integer.MIN_VALUE;
import static java.util.UUID.randomUUID;

@Slf4j
@AppFilter
@Order(MIN_VALUE)
public class LogFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }
    
    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        // log.info("log filter doFilter");
        MDC.put("traceId", randomUUID().toString().substring(0, 8));  // store in thread-local MDC
        
        try {
            // log.info("DispatcherType={}", request.getDispatcherType());
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            MDC.clear();  // always clear to avoid leaking across threads
        }
    }
    
    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
