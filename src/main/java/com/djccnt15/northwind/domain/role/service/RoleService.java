package com.djccnt15.northwind.domain.role.service;

import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.storage.DataCacheStorage;
import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.constants.RoleConst.SUPERADMIN;

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
        return userRoleRepo.findAll(Sort.by("name")).stream()
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
