package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.user.business.UserBusiness;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.djccnt15.northwind.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/auth")
@RequiredArgsConstructor
public class AuthApiController {
    
    private final UserBusiness business;
    
    @GetMapping("/check-session")
    public ResponseEntity<Api<UserInfoRes>> checkSession(@AuthenticationPrincipal UserSession userSession) {
        var response = business.getUserInfo(userSession);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PostMapping("/login/success")
    public ResponseEntity<Api<UserInfoRes>> loginSuccess(@AuthenticationPrincipal UserSession userSession) {
        var response = business.getUserInfo(userSession);
        return ResponseEntity.ok(Api.OK(response));
    }
}
