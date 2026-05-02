package com.djccnt15.northwind.domain.user.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.domain.user.Service.UserService;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminUserBusiness {
    
    private final UserService userService;
    private final UserConverter userConverter;
    
    public List<UserInfoRes> getAllUsers(int page, int size, String kw) {
        var userList = userService.getAllUsers(page, size, kw);
        return userList.stream().map(userConverter::toResponse).toList();
    }
}
