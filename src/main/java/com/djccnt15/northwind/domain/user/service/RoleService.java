package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final UserRoleRepo userRoleRepo;
    
    public List<UserRoleEntity> getRoles(List<String> names) {
        return userRoleRepo.findAllByNameInOrderByName(names);
    }
    
    public List<String> getAllRoles() {
        return userRoleRepo.findAll().stream()
            .map(UserRoleEntity::getName)
            .filter(name -> !name.equals("SUPERADMIN")).toList();
    }
}
