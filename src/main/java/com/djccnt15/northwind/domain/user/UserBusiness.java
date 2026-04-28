package com.djccnt15.northwind.domain.user;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Transactional
    public void createUser(SignupReq request) {
        userService.validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());
        userService.validateEmailNotExists(request.getEmail());
        userService.validateUsernameNotExists(request.getUsername());
        var userEntity = userService.createUser(request);
        userService.setUserBasicRole(userEntity);
    }
}
