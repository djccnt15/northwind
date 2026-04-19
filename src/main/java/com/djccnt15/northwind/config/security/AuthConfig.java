package com.djccnt15.northwind.config.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {
    
    private final AuthService authService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // TODO. 초기 개발 시에는 disable, 운영 시에는 설정 필요
            .cors(cors -> cors.configurationSource(corsConfig()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/statics/**", "/assets/**").permitAll()
                .requestMatchers("/css/**", "/favicon.*", "/*.ico").permitAll()
                .requestMatchers("/api/login", "/api/signup", "/api/auth/check-session").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // TODO. production에서는 관리자 권한 필요한 것으로 변경
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/login")
                // 성공 시 리다이렉트하지 않고 200 응답만 반환
                .successHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                // 실패 시 401 응답 반환
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })
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
