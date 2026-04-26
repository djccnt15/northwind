package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    
    public UserInfoRes getUserInfo(UserSession userSession) {
        return userConverter.toResponse(userSession);
    }
}
