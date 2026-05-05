package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.domain.user.Service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminRoleBusiness {
    
    private final RoleService roleService;
    
    public List<String> getAllRoles() {
        return roleService.getAllRoles();
    }
}
