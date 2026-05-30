package com.djccnt15.northwind.domain.user.converter;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.TeamEntity;
import com.djccnt15.northwind.domain.user.model.SessionInfoRes;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import com.djccnt15.northwind.global.annotation.Converter;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static com.djccnt15.northwind.global.util.UserUtil.getRoleName;

@Slf4j
@Converter
@RequiredArgsConstructor
public class UserConverter {
    
    private final PasswordEncoder encoder;
    
    public SessionInfoRes toResponse(UserSession userSession) {
        return SessionInfoRes.builder()
            .id(userSession.getId())
            .username(userSession.getUsername())
            .authorities(userSession.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList())
            .build();
    }
    
    public UserInfoRes toResponse(AppUserEntity entity) {
        var authorities = Optional.ofNullable(entity.getAppUserRole())
            .orElse(Collections.emptySet()).stream()
            .map(it -> getRoleName(it.getUserRole().getName())).toList();
        
        var teamName = Optional.ofNullable(entity.getTeam())
            .map(TeamEntity::getName)
            .orElse(null);
        
        return UserInfoRes.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .authorities(authorities)
            .isEnabled(entity.isVerified())
            .liveUntil(entity.getLiveUntil())
            .passwordChangedAt(entity.getPasswordChangedAt())
            .loginFailedCount(entity.getLoginFailedCount())
            .lastLoginAt(entity.getLastLoginAt())
            .team(teamName)
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
