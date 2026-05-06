package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.datacache.DataCacheStorage;
import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.comm.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.constants.RoleConst.SUPERADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final UserRoleRepo userRoleRepo;
    private final DataCacheStorage dataCacheStorage;
    
    public List<UserRoleEntity> getRoles(List<String> names) {
        return userRoleRepo.findAllByNameInOrderByName(names);
    }
    
    public List<String> getAllRoles() {
        return userRoleRepo.findAll().stream()
            .map(UserRoleEntity::getName)
            .filter(name -> !name.equals(SUPERADMIN)).toList();
    }
    
    public void validateNotSuperAdmin(Long userId) {
        var superAdmins = dataCacheStorage.getSuperAdmins();
        if (superAdmins.stream().anyMatch(entity -> entity.getId().equals(userId))) {
            throw new ApiException(BAD_REQUEST, "Cannot modify roles of a super admin user");
        }
    }
}
