package com.djccnt15.northwind.domain.user;

import com.djccnt15.northwind.annotation.Converter;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Converter
@RequiredArgsConstructor
public class UserConverter {
    
    private final PasswordEncoder encoder;
    
    public UserInfoRes toResponse(UserSession userSession) {
        return UserInfoRes.builder()
            .id(userSession.getId())
            .username(userSession.getUsername())
            .authorities(userSession.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList())
            .build();
    }
    
    public AppUserEntity toEntity(SignupReq request) {
        return AppUserEntity.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(encoder.encode(request.getPassword()))
            .build();
    }
}
