package com.djccnt15.northwind.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app")
@AllArgsConstructor
public class ConfigProperties {
    
    private final RememberMeProperties rememberMe;
}
