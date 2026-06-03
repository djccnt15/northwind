package com.djccnt15.northwind.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <p>CSRF 토큰을 쿠키에 저장하는 필터</p>
 * <p>@Component를 사용해서 등록하면 서블릿 컨테이너 레벨에서 전역 필터로 등록됨</p>
 * <p>Spring Security의 CsrfFilter보다 뒤에 실행되어야 하므로, SecurityFilterChain 설정에서 addFilterAfter로 명시적으로 추가해야 함</p>
 * <p>CsrfToken이 Request Attribute에 저장된 시점은 CsrfFilter가 실행된 이후이므로, 이 필터는 CsrfFilter 다음에 위치해야 함</p>
 */
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
