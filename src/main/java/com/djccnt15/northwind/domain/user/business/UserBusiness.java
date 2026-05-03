package com.djccnt15.northwind.domain.user.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.Service.UserRoleService;
import com.djccnt15.northwind.domain.user.Service.UserService;
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
    private final UserRoleService userRoleService;
    
    public UserInfoRes getUserInfo(UserSession userSession) {
        return userConverter.toResponse(userSession);
    }
    
    public void checkEmailExists(String email) {
        userService.validateEmailNotExists(email);
    }
    
    @Transactional
    public UserInfoRes createUser(SignupReq request) {
        userService.validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());
        userService.validateEmailNotExists(request.getEmail());
        userService.validateUsernameNotExists(request.getUsername());
        var userEntity = userService.createUser(request);
        var userRoleEntity = userRoleService.getUserRole("USER");
        userRoleService.assignRoleToUser(userEntity, userRoleEntity);
        return userConverter.toResponse(userEntity);
    }
    
    public UserInfoRes updateProfile(
        UserSession userSession,
        Long userId,
        SignupReq request
    ) {
        userService.validateUserId(userSession, userId);
        userService.validateEmailNotExists(request.getEmail(), userSession.getId());
        userService.validateUsernameNotExists(request.getUsername(), userSession.getId());
        
        var entity = userService.getUser(userSession.getId());
        userService.updateProfile(entity, request);
        
        userSession.setUsername(entity.getUsername());
        userSession.setEmail(entity.getEmail());
        return userConverter.toResponse(entity);
    }
    
    public UserInfoRes updatePassword(
        UserSession userSession,
        Long userId,
        SignupReq request
    ) {
        userService.validateUserId(userSession, userId);
        userService.validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());
        
        var entity = userService.getUser(userSession.getId());
        userService.updatePassword(entity, request.getPassword());
        
        userSession.setPassword(entity.getPassword());
        return userConverter.toResponse(entity);
    }
}
