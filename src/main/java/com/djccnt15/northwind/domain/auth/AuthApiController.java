package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.code.StatusCode;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.UserInfo;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.djccnt15.northwind.constants.RouteConst.API_VER_1;

@RestController
@RequestMapping(API_VER_1 + "/auth")
@RequiredArgsConstructor
public class AuthApiController {
    
    private final UserService service;
    
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(@AuthenticationPrincipal UserSession userSession) {
        return ResponseEntity.ok(service.getUserInfo(userSession));
    }
    
    @PostMapping("/login/success")
    public ResponseEntity<UserInfo> loginSuccess(@AuthenticationPrincipal UserSession userSession) {
        return ResponseEntity.ok(service.getUserInfo(userSession));
    }
    
    @PostMapping("/login/fail")
    public ResponseEntity<?> loginFail(HttpServletRequest request) {
        var exception = (AuthenticationException) request.getAttribute("exception");
        String message;
        if (exception instanceof BadCredentialsException) {
            message = "Invalid username or password";
        } else if (exception instanceof DisabledException) {
            message = "Account is disabled";
        } else if (exception instanceof LockedException) {
            message = "Account is locked";
        } else {
            message = exception != null ? exception.getMessage() : "Authentication failed";
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
    
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unauthorized")
    public ResponseEntity<?> unauthorized() {
        throw new ApiException(StatusCode.UNAUTHORIZED, "Authentication is required");
    }
    
    @GetMapping("/forbidden")
    public ResponseEntity<?> forbidden() {
        throw new ApiException(StatusCode.FORBIDDEN, "Access denied");
    }
}
