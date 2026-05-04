package com.djccnt15.northwind.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RememberMeProperties {
    
    private final String key;
    private final int tokenValiditySeconds;
}
