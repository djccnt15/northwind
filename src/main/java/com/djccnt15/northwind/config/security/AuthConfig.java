package com.djccnt15.northwind.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.djccnt15.northwind.constants.RouteConst.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthConfig {
    
    private final AuthService authService;
    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;
    private final LogoutHandler logoutHandler;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // TODO. 초기 개발 시에는 disable, 운영 시에는 설정 필요
            .cors(cors -> cors.configurationSource(corsConfig()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .requestMatchers(PUBLIC_AUTH_PATHS).permitAll()
                .requestMatchers(SWAGGER_PATHS).permitAll()  // TODO. production에서는 관리자 권한 필요한 것으로 변경
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl(API_VER_1 + "/login")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl(API_VER_1 + "/logout")
                .logoutSuccessHandler(logoutHandler)
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // 중복 로그인 제한
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
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        var authProvider = new DaoAuthenticationProvider(authService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}
