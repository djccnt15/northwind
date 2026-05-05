package com.djccnt15.northwind.config.security.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class UserSession implements UserDetails {
    
    private Long id;
    
    private String username;
    
    private String password;
    
    private String email;
    
    private Collection<? extends GrantedAuthority> authorities;
    
    private boolean isEnabled;  // TODO. implement email verification to set this to true
    
    private LocalDateTime liveUntil;
    
    private LocalDateTime passwordChangedAt;
    
    private int loginFailedCount;
    
    private int loginFailureLimit;
    
    private boolean isSuperAdmin;
    
    @Builder.Default
    private boolean isBanned = false;
    
    @Override  // DisabledException. 계정 비활성화(관리자가 계정 정지 등)
    public boolean isEnabled() {
        return isEnabled || isSuperAdmin;
    }
    
    @Override  // LockedException. 계정 잠금(비밀번호 수회 오류 등)
    public boolean isAccountNonLocked() {
        return (loginFailedCount < loginFailureLimit) || isSuperAdmin;
    }
    
    @Override  // AccountExpiredException. 계정 만료(기간제 회원권 만료 등)
    public boolean isAccountNonExpired() {
        return (liveUntil == null || liveUntil.isAfter(LocalDateTime.now())) || isSuperAdmin;
    }
    
    @Override  // CredentialsExpiredException. 인증 정보 만료(장기간 비밀번호 미변경 등)
    public boolean isCredentialsNonExpired() {
        // return (passwordChangedAt == null
        //     || passwordChangedAt.isAfter(LocalDateTime.now().minusMonths(6)))
        //     || isSuperAdmin;
        return true;
    }
}
