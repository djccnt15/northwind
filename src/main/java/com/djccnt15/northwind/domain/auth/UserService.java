package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.code.StatusCode;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.domain.auth.model.SignupReq;
import com.djccnt15.northwind.domain.auth.model.UserInfoRes;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserConverter userConverter;
    private final AppUserRepo userRepo;
    
    public UserInfoRes getUserInfo(UserSession userSession) {
        return userConverter.toResponse(userSession);
    }
    
    public void checkEmailExists(String email) {
        userRepo.findFirstByEmail(email)
            .ifPresent(u -> {
                throw new ApiException(StatusCode.BAD_REQUEST, "Email already exists");
            });
    }
    
    public void signup(SignupReq request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(StatusCode.BAD_REQUEST, "Password and confirm password do not match");
        }
        
        var userEntity = userConverter.toEntity(request);
        
        try {
            userRepo.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            handleDuplicateKeyException(e);
        }
    }
    
    private void handleDuplicateKeyException(DataIntegrityViolationException e) {
        log.error("Duplicate key error", e);

        // TODO. change unique constraint names to be more descriptive
        switch (e.getMostSpecificCause().getMessage()) {
            case String s when s.contains("UK1j9d9a06i600gd43uu3km82jw") ->
                throw new ApiException(StatusCode.BAD_REQUEST, "Email already exists");
            case String s when s.contains("UK3k4cplvh82srueuttfkwnylq0") ->
                throw new ApiException(StatusCode.BAD_REQUEST, "Username already exists");
            default -> throw new ApiException(StatusCode.SERVER_ERROR, "An unexpected error occurred");
        }
    }
}
