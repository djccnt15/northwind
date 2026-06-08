package com.djccnt15.northwind.global.config.security;

import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
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

import static com.djccnt15.northwind.global.code.StatusCode.*;
import static com.djccnt15.northwind.global.constants.RoleConst.SUPERADMIN;
import static com.djccnt15.northwind.global.util.UserUtil.getRoleName;
import static com.djccnt15.northwind.domain.auth.validation.AuthErrorConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final AppUserRepo repository;
    private final MessageUtil messageUtil;

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
            .authorities(authorities)
            .isEnabled(entity.isVerified())
            .liveUntil(entity.getLiveUntil())
            .passwordChangedAt(entity.getPasswordChangedAt())
            .loginFailedCount(entity.getLoginFailedCount())
            .loginFailureLimit(loginFailureLimit)
            .isSuperAdmin(isSuperAdmin)
            .build();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void handleLoginSuccess(Long id) {
        repository.handleLoginSuccess(id, LocalDateTime.now());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void increaseFailedCount(String username) {
        repository.increaseLoginFailedCount(username);
    }
    
    public String getErrorMessage(AuthenticationException exception) {
        return switch (exception) {
            case BadCredentialsException ignored -> messageUtil.getMessage(BAD_CREDENTIALS_ERR_MSG);
            case DisabledException ignored -> messageUtil.getMessage(ACCOUNT_DISABLED_ERR_MSG);
            case LockedException ignored -> messageUtil.getMessage(ACCOUNT_LOCKED_ERR_MSG);
            case AccountExpiredException ignored -> messageUtil.getMessage(ACCOUNT_EXPIRED_ERR_MSG);
            case CredentialsExpiredException ignored -> messageUtil.getMessage(CREDENTIALS_EXPIRED_ERR_MSG);
            case null, default -> messageUtil.getMessage(CONTACT_ADMINISTRATOR_ERR_MSG);
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
