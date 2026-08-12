package com.djccnt15.northwind.global.config.actuator;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {
    
    @Bean
    InMemoryHttpExchangeRepository inMemoryHttpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
