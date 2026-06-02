package com.djccnt15.northwind.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        // Request Attribute에 저장된 CsrfToken을 가져옴 (이 시점에 토큰이 실제 생성/불러와짐)
        var csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        
        if (csrfToken != null) {
            // 토큰을 응답 헤더나 쿠키에 명시적으로 바인딩하기 위해 세이브 처리
            // CookieCsrfTokenRepository를 사용 중이므로 자동으로 XSRF-TOKEN 쿠키가 생성됩니다.
            csrfToken.getToken();
        }
        
        filterChain.doFilter(request, response);
    }
}
