package com.djccnt15.northwind.config.security.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Builder
public class UserSession implements UserDetails {
    
    private Long id;
    
    private String username;
    
    private String password;
    
    private Collection<? extends GrantedAuthority> authorities;
}
