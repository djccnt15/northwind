package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.comm.code.StatusCode;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.UserInfoRes;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Api<?>> checkSession(@AuthenticationPrincipal UserSession userSession) {
        return ResponseEntity.ok(Api.OK(service.getUserInfo(userSession)));
    }
    
    @PostMapping("/login/success")
    public ResponseEntity<Api<UserInfoRes>> loginSuccess(@AuthenticationPrincipal UserSession userSession) {
        return ResponseEntity.ok(Api.OK(service.getUserInfo(userSession)));
    }
    
    @PostMapping("/login/fail")
    public ResponseEntity<Api<?>> loginFail(HttpServletRequest request) {
        var exception = (AuthenticationException) request.getAttribute("exception");
        var message = switch (exception) {
            case null -> "Authentication failed";
            case BadCredentialsException ignored -> "Invalid username or password";
            case DisabledException ignored -> "Account is disabled";
            case LockedException ignored -> "Account is locked";
            default -> exception.getMessage();
        };
        throw new ApiException(StatusCode.UNAUTHORIZED, message);
    }
    
    @GetMapping("/logout")
    public ResponseEntity<Api<?>> logout() {
        return ResponseEntity.ok(Api.OK(null));
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
