package com.djccnt15.northwind.domain.auth.business;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;

import static com.djccnt15.northwind.comm.code.StatusCode.*;

@Slf4j
@Business
@RequiredArgsConstructor
public class AuthBusiness {
    
    public void handleLoginFailure(HttpServletRequest request) {
        var exception = (AuthenticationException) request.getAttribute("exception");
        
        var message = switch (exception) {
            case BadCredentialsException ignored -> "Invalid username or password";
            case DisabledException ignored -> "Account is disabled";
            case LockedException ignored -> "Account is locked";
            case AccountExpiredException ignored -> "Account has expired";
            case CredentialsExpiredException ignored -> "Credentials have expired";
            case null, default -> "Authentication failed, Please contact to admin";
        };
        
        switch (exception) {
            case null -> throw new ApiException(SERVER_ERROR, message);
            case BadCredentialsException ignored -> throw new ApiException(UNAUTHORIZED, message);
            default -> throw new ApiException(FORBIDDEN, message);
        }
    }
}
