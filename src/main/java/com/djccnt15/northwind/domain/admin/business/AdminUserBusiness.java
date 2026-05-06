package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.domain.model.ListBodyReq;
import com.djccnt15.northwind.domain.model.ListCountRes;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import com.djccnt15.northwind.domain.user.service.RoleService;
import com.djccnt15.northwind.domain.user.service.UserRoleService;
import com.djccnt15.northwind.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminUserBusiness {
    
    private final UserService userService;
    private final UserConverter userConverter;
    private final UserRoleService userRoleService;
    private final RoleService roleService;
    
    public ListCountRes<UserInfoRes> getAllUsers(int page, int size, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var userList = userService.getAllUsers(page, size, kw).stream()
            .map(userConverter::toResponse).toList();
        var totalCounts = userService.getUserCount(kw);
        
        return ListCountRes.<UserInfoRes>builder()
            .list(userList)
            .totalCounts(totalCounts)
            .build();
    }
    
    public UserInfoRes updateUser(Long userId, SignupReq request) {
        userService.validateEmailNotExists(request.getEmail(), userId);
        userService.validateUsernameNotExists(request.getUsername(), userId);
        var entity = userService.getUser(userId);
        userService.updateProfile(entity, request);
        return userConverter.toResponse(entity);
    }
    
    public UserInfoRes resetPassword(Long userId) {
        var entity = userService.getUser(userId);
        userService.resetPassword(entity);
        return userConverter.toResponse(entity);
    }
    
    @Transactional
    public UserInfoRes updateUserRoles(Long userId, ListBodyReq<String> request) {
        roleService.validateNotSuperAdmin(userId);
        
        var userEntity = userService.getUser(userId);
        userRoleService.deleteUserRoles(userEntity);
        userEntity.setAppUserRole(new HashSet<>());
        
        var roleEntities = roleService.getRoles(request.getList());
        var updatedUser = userRoleService.assignRolesToUser(userEntity, roleEntities);
        return userConverter.toResponse(updatedUser);
    }
}
