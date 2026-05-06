package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    private final AppUserRepo userRepo;
    private final PasswordEncoder encoder;
    
    @Value("${app.defaultPw:1234}")
    private String defaultPw;
    
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
        return userRepo.findById(userId)
            .orElseThrow(() -> new ApiException(BAD_REQUEST, "User not found"));
    }
    
    public AppUserEntity updateProfile(AppUserEntity entity, SignupReq request) {
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setVerified(request.isEnabled());
        entity.setLiveUntil(request.getLiveUntil());
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
    
    public void resetPassword(AppUserEntity entity) {
        entity.setPassword(encoder.encode(defaultPw));
        entity.setPasswordChangedAt(LocalDateTime.now());
        entity.setLoginFailedCount(0);
        userRepo.save(entity);
    }
}
