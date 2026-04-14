package com.djccnt15.northwind.filter;

import com.djccnt15.northwind.annotation.AppFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.lang.Integer.MIN_VALUE;

@Slf4j
@AppFilter
@Order(MIN_VALUE + 1)
public class SpaWebFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    )
        throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // 제외할 조건 설정
        // - /api로 시작하는 요청
        // - 파일 확장자가 있는 요청 (js, css, png 등 정적 자원)
        // - 이미 index.html로 포워딩된 요청 (무한 루프 방지)
        if (isApiRequest(path) || isStaticResource(path) || isForwarded(path)
            || isSwaggerUiRequest(path)  // TODO. rm for production
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 나머지는 index.html로 forward
        request.getRequestDispatcher("/index.html").forward(request, response);
    }
    
    private boolean isSwaggerUiRequest(String path) {
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }
    
    private boolean isApiRequest(String path) {
        return path.startsWith("/api");
    }
    
    private boolean isStaticResource(String path) {
        // 보통 점(.)이 포함된 경로는 파일 요청으로 간주합니다.
        return path.contains(".");
    }
    
    private boolean isForwarded(String path) {
        return path.equals("/index.html");
    }
}
