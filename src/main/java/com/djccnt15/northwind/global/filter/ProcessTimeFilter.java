/*
package com.djccnt15.northwind.global.filter;

import com.djccnt15.northwind.global.annotation.AppFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static java.lang.Integer.MIN_VALUE;
import static org.springframework.util.PatternMatchUtils.simpleMatch;

@Slf4j
@AppFilter
@Order(MIN_VALUE + 1)
public class ProcessTimeFilter implements Filter {
    
    private static final String[] excludeList = {
        "/*.ico", "/bootstrap.min", "*.js", "*.css"
    };
    
    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        // cashing wrapper for request/response which will be consumed
        var req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        var res = new ContentCachingResponseWrapper((HttpServletResponse) response);
        
        var startTime = System.currentTimeMillis();
        
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            var isExcludeUri = simpleMatch(excludeList, req.getRequestURI().toLowerCase());
            if (!isExcludeUri) {
                var processTime = (System.currentTimeMillis() - startTime) + "ms";
                var hiddenMethod = req.getParameter("_method");
                var actualMethod = (hiddenMethod != null) ? hiddenMethod : req.getMethod();
                
                log.info(
                    "X-Process-Time: {}, uri: {}, method: {}",
                    processTime,
                    req.getRequestURI(),
                    actualMethod
                );
                res.addHeader("X-Process-Time", processTime);  // add process time to response header
            }
            res.copyBodyToResponse();  // restore consumed response
        }
    }
}
*/
