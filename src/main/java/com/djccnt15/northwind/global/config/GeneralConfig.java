package com.djccnt15.northwind.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  // enable @Scheduled annotations
@ConfigurationPropertiesScan  // enable @ConfigurationProperties annotation
public class GeneralConfig {
}
