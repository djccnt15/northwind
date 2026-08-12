package com.djccnt15.northwind.global.config.actuator;

import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@ManagementContextConfiguration
@Profile("prod")
public class ActuatorSecurityProdConfig {
    
    @Bean
    @Order(0)
    SecurityFilterChain actuatorSecurityFilterChain(
        HttpSecurity http,
        SecurityProperties securityProperties,
        PasswordEncoder passwordEncoder
    ) throws Exception {
        var user = securityProperties.getUser(); // PROMETHEUS_USER/PROMETHEUS_PASSWORD/roles:METRICS
        var actuatorUsers = new InMemoryUserDetailsManager(
            User.withUsername(user.getName())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(user.getRoles().toArray(new String[0]))
                .build()
        );
        
        // 지역 변수로만 유지 — @Bean으로 등록하면 authenticationManager()와 충돌
        var actuatorAuthProvider = new DaoAuthenticationProvider(actuatorUsers);
        actuatorAuthProvider.setPasswordEncoder(passwordEncoder);
        var actuatorAuthManager = new ProviderManager(actuatorAuthProvider);
        
        http
            .securityMatcher("/actuator/**") // Actuator 경로 전체에 적용
            .authenticationManager(actuatorAuthManager) // 전용 계정으로 인증
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll() // LB/모니터링 probe
                .anyRequest().hasRole("METRICS"))               // 전용 계정만 인증 허용
            .httpBasic(Customizer.withDefaults())   // Basic Auth 활성화
            .csrf(AbstractHttpConfigurer::disable);     // Actuator는 CSRF 보호 필요 없음
        return http.build();
    }
}
