package com.djccnt15.northwind.global.filter;

import com.djccnt15.northwind.global.annotation.AppFilter;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import java.io.IOException;

import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static java.lang.Integer.MIN_VALUE;

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
        MDC.put("traceId", getRandUuidString());  // store in thread-local MDC
        
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
