package com.djccnt15.northwind.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableScheduling  // enable @Scheduled annotations
@ConfigurationPropertiesScan  // enable @ConfigurationProperties annotation
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class GeneralConfig {
}
