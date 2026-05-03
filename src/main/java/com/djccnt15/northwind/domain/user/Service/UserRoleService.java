package com.djccnt15.northwind.domain.user.Service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.AppUserRoleRepo;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.djccnt15.northwind.comm.code.StatusCode.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
    
    private final UserRoleRepo userRoleRepo;
    private final AppUserRoleRepo appUserRoleRepo;
    
    public UserRoleEntity getUserRole(String name) {
        return userRoleRepo.findFirstByName(name).orElseThrow(() -> {
            log.error("User role not found in database name: {}", name);
            return new ApiException(SERVER_ERROR, "Please contact administrator");
        });
    }
    
    public AppUserEntity assignRoleToUser(AppUserEntity userEntity, UserRoleEntity roleEntity) {
        var appUserRoleEntity = AppUserRoleEntity.builder()
            .userRole(roleEntity)
            .build();
        userEntity.addAppUserRole(appUserRoleEntity);
        appUserRoleRepo.save(appUserRoleEntity);
        return userEntity;
    }
}
