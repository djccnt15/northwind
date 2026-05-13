package com.djccnt15.northwind.domain.user.business;

import com.djccnt15.northwind.domain.user.converter.EmployeeConverter;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import com.djccnt15.northwind.domain.user.service.EmployeeService;
import com.djccnt15.northwind.domain.user.service.UserRoleService;
import com.djccnt15.northwind.domain.user.service.UserService;
import com.djccnt15.northwind.global.annotation.Business;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import static com.djccnt15.northwind.global.constants.RoleConst.USER;

@Slf4j
@Business
@RequiredArgsConstructor
public class UserBusiness {
    
    private final UserConverter userConverter;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final EmployeeConverter employeeConverter;
    private final EmployeeService employeeService;
    
    public void checkEmailExists(String email) {
        userService.validateEmailNotExists(email);
    }
    
    @Transactional
    public UserInfoRes createUser(SignupReq request) {
        userService.validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());
        userService.validateEmailNotExists(request.getEmail());
        userService.validateUsernameNotExists(request.getUsername());
        var userEntity = userService.createUser(request);
        var userRoleEntity = userRoleService.getUserRole(USER);
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
    
    public UserInfoRes getUserInfo(UserSession userSession, Long userId) {
        userService.validateUserId(userSession, userId);
        var userEntity = userService.getFullUser(userSession.getId());
        var user = userConverter.toResponse(userEntity);

        var employeeEntity = userEntity.getEmployee();
        if (employeeEntity == null) {
            return user;
        }
        
        var employee = employeeConverter.toResponse(employeeEntity);
        user.setEmployee(employee);
        return user;
    }
    
    @Transactional
    public UserInfoRes updateInfo(
        UserSession userSession,
        Long userId,
        EmployeeReq request
    ) {
        userService.validateUserId(userSession, userId);
        var userEntity = userService.getFullUser(userSession.getId());
        
        if (userEntity.getEmployee() == null) {
            var employeeEntity = employeeService.createEmployee(request);
            userService.updateProfile(userEntity, employeeEntity);
            return userConverter.toResponse(userEntity);
        }
        
        employeeService.updateEmployee(userEntity.getEmployee(), request);
        return userConverter.toResponse(userEntity);
    }
}
