package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.config.security.model.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseEntity.ok().body(userSession.getUsername());
    }
}
