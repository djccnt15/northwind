package com.djccnt15.northwind.global.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // enable @CreatedDate/@LastModifiedDate, @CreatedBy/@LastModifiedBy
public class JpaConfig {
}
