package com.djccnt15.northwind.global.config.security;

import com.djccnt15.northwind.global.config.properties.ConfigProperties;
import com.djccnt15.northwind.global.filter.CsrfCookieFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthConfig {
    
    private final UserDetailsService authService;
    private final AuthenticationSuccessHandler authSuccessHandler;
    private final AuthenticationFailureHandler authFailureHandler;
    private final LogoutSuccessHandler logoutHandler;
    private final AuthenticationEntryPoint unauthorizedHandler;
    private final AccessDeniedHandler forbiddenHandler;
    private final ConfigProperties configProperties;
    
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(API_ALL)
            .csrf(csrf -> csrf
                .csrfTokenRepository(getCsrfTokenRepository())
                .csrfTokenRequestHandler(getCsrfRequestHandler())
                // .ignoringRequestMatchers(PUBLIC_ALL)  // API 중 인증이 필요 없는 경로는 CSRF 보호에서 제외
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfig()))
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(unauthorizedHandler, getRequestMatcher())
                .defaultAccessDeniedHandlerFor(forbiddenHandler, getRequestMatcher())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ALL).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl(PUBLIC_API_V1 + "/login")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
            )
            .rememberMe(remember -> remember
                .key(configProperties.getRememberMe().getKey())
                .tokenValiditySeconds(configProperties.getRememberMe().getTokenValiditySeconds())
            )
            .logout(logout -> logout
                .logoutUrl(API_V1 + "/logout")
                .logoutSuccessHandler(logoutHandler)
                .invalidateHttpSession(true)  // default: true. only for explicitly setting
                .clearAuthentication(true)  // default: true. only for explicitly setting
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session -> session
                .maximumSessions(1)  // 중복 로그인 제한
            );
        return http.build();
    }
    
    private static RequestMatcher getRequestMatcher() {
        return request -> request.getRequestURI().startsWith(API);
    }
    
    private static CsrfTokenRepository getCsrfTokenRepository() {
        var csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieCustomizer(customizer -> customizer
            .secure(true) // 운영(HTTPS) 환경 필수: HTTP 전송 차단
            .sameSite("Lax") // 단일 도메인 SPA에 가장 안전한 방식
            .path("/") // 전체 경로에서 쿠키 사용 가능하도록 지정
        );
        return csrfTokenRepository;
    }
    
    private static CsrfTokenRequestHandler getCsrfRequestHandler() {
        var handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName("_csrf");
        return handler;
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain spaSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(ALL)
            .csrf(AbstractHttpConfigurer::disable)  // SPA는 API와 별도의 보안 설정 적용, CSRF 보호는 API 필터 체인에서 처리
            .cors(cors -> cors.configurationSource(corsConfig()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                // .requestMatchers(SWAGGER_PATHS).permitAll()  // only for early development, restrict access for production
                .requestMatchers(SWAGGER_PATHS).hasAnyAuthority("ADMIN")
                .anyRequest().permitAll()
            );
        return http.build();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    CorsConfigurationSource corsConfig() {
        var config = new CorsConfiguration();
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);  // 쿠키 허용
        config.setMaxAge(3600L);  // preflight 요청 캐싱 시간 (초)
        
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ALL, config);
        return source;
    }
    
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        var authProvider = new DaoAuthenticationProvider(authService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}
