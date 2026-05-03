package com.djccnt15.northwind.domain.user.Service;

import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.AppUserRoleRepo;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.comm.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.comm.code.StatusCode.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    private final AppUserRepo userRepo;
    private final PasswordEncoder encoder;
    
    public void validateUserId(UserSession userSession, Long userId) {
        if (!userSession.getId().equals(userId)) {
            throw new ApiException(BAD_REQUEST, "You are not authorized to perform this action");
        }
    }
    
    public void validatePasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ApiException(BAD_REQUEST, "Password and confirm password do not match");
        }
    }
    
    public void validateEmailNotExists(String email) {
        userRepo.findFirstByEmail(email)
            .ifPresent(u -> {throw new ApiException(BAD_REQUEST, "Email already exists");});
    }
    
    public void validateEmailNotExists(String email, Long userId) {
        userRepo.findFirstByEmailAndIdNot(email, userId)
            .ifPresent(u -> {throw new ApiException(BAD_REQUEST, "Email already exists");});
    }
    
    public void validateUsernameNotExists(String username) {
        userRepo.findFirstByUsername(username)
            .ifPresent(u -> {throw new ApiException(BAD_REQUEST, "Username already exists");});
    }
    
    public void validateUsernameNotExists(String username, Long userId) {
        userRepo.findFirstByUsernameAndIdNot(username, userId)
            .ifPresent(u -> {throw new ApiException(BAD_REQUEST, "Username already exists");});
    }
    
    public AppUserEntity createUser(SignupReq request) {
        var userEntity = userConverter.toEntity(request);
        userRepo.save(userEntity);
        return userEntity;
    }
    
    public AppUserEntity getUser(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> {
            log.error("User with id {} not found", userId);
            return new ApiException(BAD_REQUEST, "User not found");
        });
    }
    
    public AppUserEntity updateProfile(AppUserEntity entity, SignupReq request) {
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        userRepo.save(entity);
        return entity;
    }
    
    public AppUserEntity updatePassword(AppUserEntity entity, String password) {
        entity.setPassword(encoder.encode(password));
        userRepo.save(entity);
        return entity;
    }
    
    public List<AppUserEntity> getAllUsers(int page, int size, String keyword) {
        var pageable = PageRequest.of(page, size, Sort.by("id"));
        return userRepo.findWithRoleByUsernameLikeOrEmailLike(keyword, keyword, pageable);
    }
    
    public Integer getUserCount(String keyword) {
        return userRepo.countByUsernameLikeOrEmailLike(keyword, keyword);
    }
}
