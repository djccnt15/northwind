package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.AppUserRoleRepo;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.SERVER_ERROR;
import static com.djccnt15.northwind.domain.role.validation.UserRoleErrorConst.CONTACT_ADMINISTRATOR_ERR_MSG;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
    
    private final UserRoleRepo userRoleRepo;
    private final AppUserRoleRepo appUserRoleRepo;
    private final MessageUtil messageUtil;

    public UserRoleEntity getUserRole(String name) {
        return userRoleRepo.findFirstByName(name)
            .orElseThrow(() -> new ApiException(SERVER_ERROR, messageUtil.getMessage(CONTACT_ADMINISTRATOR_ERR_MSG)));
    }
    
    public AppUserEntity assignRoleToUser(AppUserEntity userEntity, UserRoleEntity roleEntity) {
        var appUserRoleEntity = AppUserRoleEntity.builder()
            .userRole(roleEntity)
            .build();
        userEntity.addAppUserRole(appUserRoleEntity);
        appUserRoleRepo.save(appUserRoleEntity);
        return userEntity;
    }
    
    public AppUserEntity assignRolesToUser(AppUserEntity userEntity, List<UserRoleEntity> roleEntities) {
        roleEntities.stream()
            .map(it -> AppUserRoleEntity.builder().userRole(it).build())
            .forEach(userEntity::addAppUserRole);
        appUserRoleRepo.saveAll(userEntity.getAppUserRole());
        return userEntity;
    }
    
    public AppUserEntity deleteUserRoles(AppUserEntity userEntity) {
        appUserRoleRepo.deleteAllByAppUser(userEntity);
        userEntity.resetAppUserRole();
        return userEntity;
    }
}
