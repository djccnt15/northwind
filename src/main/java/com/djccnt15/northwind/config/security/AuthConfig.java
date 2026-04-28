package com.djccnt15.northwind.config.security;

import com.djccnt15.northwind.config.RememberMeProperties;
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
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.djccnt15.northwind.constants.RouteConst.*;

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
    private final RememberMeProperties rememberMeProperties;
    
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(API_ALL)
            .csrf(AbstractHttpConfigurer::disable)  // TODO. 초기 개발 시에는 disable, 운영 시에는 설정 필요
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
                .key(rememberMeProperties.getKey())
                .tokenValiditySeconds(rememberMeProperties.getTokenValiditySeconds())
            )
            .logout(logout -> logout
                .logoutUrl(API_V1 + "/logout")
                .logoutSuccessHandler(logoutHandler)
                .invalidateHttpSession(true)  // default: true. only for explicitly setting
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
    
    @Bean
    @Order(2)
    public SecurityFilterChain spaSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(ALL)
            .csrf(AbstractHttpConfigurer::disable)    // TODO. 초기 개발 시에는 disable, 운영 시에는 설정 필요
            .cors(cors -> cors.configurationSource(corsConfig()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .requestMatchers(SWAGGER_PATHS).permitAll()  // TODO. production에서는 관리자 권한 필요한 것으로 변경
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
