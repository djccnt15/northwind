package com.djccnt15.northwind.global.config.jpa;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // enable @CreatedDate/@LastModifiedDate, @CreatedBy/@LastModifiedBy
public class JpaConfig {
}
