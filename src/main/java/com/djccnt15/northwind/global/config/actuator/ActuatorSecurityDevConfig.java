package com.djccnt15.northwind.global.config.actuator;

import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@ManagementContextConfiguration
@Profile("dev")
public class ActuatorSecurityDevConfig {
    
    @Bean
    @Order(0)
    public SecurityFilterChain actuatorSecurityFilterChain(
        HttpSecurity http,
        SecurityProperties securityProperties,
        PasswordEncoder passwordEncoder
    ) throws Exception {
        
        http
            .securityMatcher("/actuator/**") // Actuator 경로 전체에 적용
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable); // Actuator는 CSRF 보호 필요 없음
        return http.build();
    }
}
