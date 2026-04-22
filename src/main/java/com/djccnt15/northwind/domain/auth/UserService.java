package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    
    public UserInfo getUserInfo(UserSession userSession) {
        if (userSession == null) return null;
        
        return UserInfo.builder()
            .id(userSession.getId())
            .username(userSession.getUsername())
            .build();
    }
}
