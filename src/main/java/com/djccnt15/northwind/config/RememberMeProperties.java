package com.djccnt15.northwind.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.remember-me")
public class RememberMeProperties {
    
    private final String key;
    private final int tokenValiditySeconds;
}
