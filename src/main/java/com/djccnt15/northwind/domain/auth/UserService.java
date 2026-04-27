package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.domain.auth.model.SignupReq;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.djccnt15.northwind.comm.code.StatusCode.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    private final AppUserRepo userRepo;
    
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
    
    public void createUser(SignupReq req) {
        var userEntity = userConverter.toEntity(req);
        userRepo.save(userEntity);
    }
}
