package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.annotation.Converter;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.UserInfoRes;
import org.springframework.security.core.GrantedAuthority;

@Converter
public class UserConverter {
    
    public UserInfoRes toResponse(UserSession userSession) {
        return UserInfoRes.builder()
            .id(userSession.getId())
            .username(userSession.getUsername())
            .authorities(userSession.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList())
            .build();
    }
}
