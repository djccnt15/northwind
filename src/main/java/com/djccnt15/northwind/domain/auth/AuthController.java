package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.auth.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(@AuthenticationPrincipal UserSession userSession) {
        if (userSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
        
        var userInfo = UserInfo.builder()
            .id(userSession.getId())
            .username(userSession.getUsername())
            .build();
        return ResponseEntity.ok().body(userInfo);
    }
    
    @PostMapping("/success")
    public ResponseEntity<UserInfo> loginSuccess(@AuthenticationPrincipal UserSession userSession) {
        var userInfo = UserInfo.builder()
            .id(userSession.getId())
            .username(userSession.getUsername())
            .build();
        return ResponseEntity.ok(userInfo);
    }
}
