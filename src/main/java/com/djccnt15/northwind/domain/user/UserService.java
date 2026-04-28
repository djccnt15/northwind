package com.djccnt15.northwind.domain.user;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.AppUserRoleRepo;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.djccnt15.northwind.comm.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.comm.code.StatusCode.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    private final AppUserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final AppUserRoleRepo appUserRoleRepo;
    
    public void validatePasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ApiException(BAD_REQUEST, "Password and confirm password do not match");
        }
    }
    
    public void validateEmailNotExists(String email) {
        userRepo.findFirstByEmail(email)
            .ifPresent(u -> {throw new ApiException(BAD_REQUEST, "Email already exists");});
    }
    
    public void validateUsernameNotExists(String username) {
        userRepo.findSimpleFirstByUsername(username)
            .ifPresent(u -> {throw new ApiException(BAD_REQUEST, "Username already exists");});
    }
    
    public AppUserEntity createUser(SignupReq req) {
        var userEntity = userConverter.toEntity(req);
        userRepo.save(userEntity);
        return userEntity;
    }
    
    public void setUserBasicRole(AppUserEntity userEntity) {
        var role = userRoleRepo.findFirstByName("USER")
            .orElseThrow(() -> {
                log.error("Default role USER not found in database");
                return new ApiException(SERVER_ERROR, "Please contact administrator");
            });
        var appUserRoleEntity = AppUserRoleEntity.builder()
            .appUser(userEntity)
            .userRole(role)
            .build();
        appUserRoleRepo.save(appUserRoleEntity);
    }
}
