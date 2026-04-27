package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.SignupReq;
import com.djccnt15.northwind.domain.auth.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Business
@RequiredArgsConstructor
public class UserBusiness {
    
    private final UserConverter userConverter;
    private final UserService userService;
    
    public UserInfoRes getUserInfo(UserSession userSession) {
        return userConverter.toResponse(userSession);
    }
    
    public void checkEmailExists(String email) {
        userService.validateEmailNotExists(email);
    }
    
    public void createUser(SignupReq request) {
        userService.validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());
        userService.validateEmailNotExists(request.getEmail());
        userService.validateUsernameNotExists(request.getUsername());
        userService.createUser(request);
    }
}
