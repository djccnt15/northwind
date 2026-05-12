package com.djccnt15.northwind.domain.user.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SessionInfoRes {
    
    private Long id;
    
    private String username;
    
    private List<String> authorities;
}
