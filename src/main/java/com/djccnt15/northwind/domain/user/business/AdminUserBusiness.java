package com.djccnt15.northwind.domain.user.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.domain.user.Service.UserService;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.model.ListCountRes;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminUserBusiness {
    
    private final UserService userService;
    private final UserConverter userConverter;
    
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
}
