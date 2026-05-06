package com.djccnt15.northwind.config.security;

import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.djccnt15.northwind.comm.code.StatusCode.*;
import static com.djccnt15.northwind.constants.RoleConst.SUPERADMIN;
import static com.djccnt15.northwind.util.UserUtil.getRoleName;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final AppUserRepo repository;

    @Value("${app.loginFailureLimit:6}")
    private int loginFailureLimit;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var entity = repository.findWithRoleFirstByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        
        var authorities = entity.getAppUserRole().stream()
            .map(it -> getRoleName(it.getUserRole().getName()))
            .map(SimpleGrantedAuthority::new).toList();

        var isSuperAdmin = entity.getAppUserRole().stream()
            .anyMatch(it -> it.getUserRole().getName().equals(SUPERADMIN));
        
        return UserSession.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .email(entity.getEmail())
            .authorities(authorities)
            .isEnabled(entity.isVerified())
            .liveUntil(entity.getLiveUntil())
            .passwordChangedAt(entity.getPasswordChangedAt())
            .loginFailedCount(entity.getLoginFailedCount())
            .loginFailureLimit(loginFailureLimit)
            .lastLoginAt(entity.getLastLoginAt())
            .isSuperAdmin(isSuperAdmin)
            .build();
    }
    
    @Transactional
    public void handleLoginSuccess(Long id) {
        repository.handleLoginSuccess(id, LocalDateTime.now());
    }
    
    @Transactional
    public void increaseFailedCount(String username) {
        repository.increaseLoginFailedCount(username);
    }
    
    public String getErrorMessage(AuthenticationException exception) {
        return switch (exception) {
            case BadCredentialsException ignored -> "Invalid username or password";
            case DisabledException ignored -> "Account is disabled";
            case LockedException ignored -> "Account is locked";
            case AccountExpiredException ignored -> "Account has expired";
            case CredentialsExpiredException ignored -> "Credentials have expired";
            case null, default -> "Authentication failed, Please contact to admin";
        };
    }
    
    public void throwException(AuthenticationException exception, String message) {
        switch (exception) {
            case null -> throw new ApiException(SERVER_ERROR, message);
            case BadCredentialsException ignored -> throw new ApiException(UNAUTHORIZED, message);
            default -> throw new ApiException(FORBIDDEN, message);
        }
    }
}
