package com.djccnt15.northwind.config.security;

import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final AppUserRepo repository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var entity = repository.findFirstByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        
        var authorities = entity.getAppUserRole().stream()
            .map(AuthService::getRoleName)
            .map(SimpleGrantedAuthority::new).toList();
        
        return UserSession.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .authorities(authorities)
            .password(entity.getPassword())
            .build();
    }
    
    private static String getRoleName(AppUserRoleEntity role) {
        var roleName = role.getUserRole().getName();
        return switch (roleName) {
            case "superadmin", "admin" -> "admin";
            case null -> "user";
            default -> roleName;
        };
    }
}
