package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import com.djccnt15.northwind.domain.model.ListBodyReq;
import com.djccnt15.northwind.domain.role.service.RoleService;
import com.djccnt15.northwind.domain.team.service.TeamService;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import com.djccnt15.northwind.domain.user.service.UserRoleService;
import com.djccnt15.northwind.domain.user.service.UserService;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final TeamService teamService;
    
    public Page<UserInfoRes> getUsers(int page, int size, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by("id"));
        var userPage = userService.getUsers(kw, pageable);
        
        var userIds = userPage.map(BaseEntity::getId).toList();
        var entities = userService.getUsers(userIds);
        var userList = entities.stream().map(userConverter::toResponse).toList();
        
        return new PageImpl<>(userList, pageable, userPage.getTotalElements());
    }
    
    @Transactional
    public UserInfoRes updateUser(Long userId, SignupReq request) {
        userService.validateEmailNotExists(request.getEmail(), userId);
        userService.validateUsernameNotExists(request.getUsername(), userId);
        var entity = userService.getUser(userId);
        var team = teamService.getTeam(request.getTeam());
        teamService.addMember(team, entity);
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
        
        var entity = userService.getUser(userId);
        userRoleService.deleteUserRoles(entity);
        entity.setAppUserRole(new HashSet<>());
        
        var roleEntities = roleService.getRoles(request.getList());
        var updatedUser = userRoleService.assignRolesToUser(entity, roleEntities);
        return userConverter.toResponse(updatedUser);
    }
}
