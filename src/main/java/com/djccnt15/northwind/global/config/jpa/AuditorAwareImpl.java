package com.djccnt15.northwind.global.config.jpa;

import com.djccnt15.northwind.global.config.security.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class AuditorAwareImpl implements AuditorAware<Long> {
    
    @Override
    public Optional<Long> getCurrentAuditor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // default value for unauthenticated case
        if (authentication == null || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        
        var usersession = (UserSession) authentication.getPrincipal();
        return Optional.of(usersession.getId());
    }
}
