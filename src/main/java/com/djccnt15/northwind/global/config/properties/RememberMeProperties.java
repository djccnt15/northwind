package com.djccnt15.northwind.global.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RememberMeProperties {
    
    private final String key;
    private final int tokenValiditySeconds;
}
