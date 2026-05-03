package com.djccnt15.northwind.domain.user.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoRes {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private List<String> authorities;
    
    private boolean isEnabled;
}
