package com.djccnt15.northwind.domain.user.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminRoleBusiness {
    
    private final UserRoleRepo userRoleRepo;
    
    public List<String> getAllRoles() {
        return userRoleRepo.findAll().stream()
            .map(UserRoleEntity::getName)
            .filter(name -> !name.equals("SUPERADMIN")).toList();
    }
}
