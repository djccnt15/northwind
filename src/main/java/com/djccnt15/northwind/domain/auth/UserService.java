package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.code.StatusCode;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.domain.auth.model.UserInfoRes;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    private final AppUserRepo userRepo;
    
    public UserInfoRes getUserInfo(UserSession userSession) {
        return userConverter.toResponse(userSession);
    }
    
    public void checkEmailExists(String email) {
        userRepo.findFirstByEmail(email)
            .ifPresent(u -> {
                throw new ApiException(StatusCode.BAD_REQUEST, "Email already exists");
            });
    }
}
