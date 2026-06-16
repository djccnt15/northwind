package com.djccnt15.northwind.domain.user.controller;

import com.djccnt15.northwind.domain.user.business.UserBusiness;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UpdateLangReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/user")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserApiController {
    
    private final UserBusiness business;
    
    @GetMapping("{userId}")
    public ResponseEntity<Api<UserInfoRes>> getUserInfo(
        @AuthenticationPrincipal UserSession userSession,
        @PathVariable Long userId
    ) {
        var response = business.getUserInfo(userSession, userId);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PatchMapping("{userId}/profile")
    public ResponseEntity<Api<UserInfoRes>> updateProfile(
        @AuthenticationPrincipal UserSession userSession,
        @PathVariable Long userId,
        @Validated(SignupReq.ProfileUpdate.class) @RequestBody SignupReq request
    ) {
        var response = business.updateProfile(userSession, userId, request);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PatchMapping("{userId}/password")
    public ResponseEntity<Api<UserInfoRes>> updatePassword(
        @AuthenticationPrincipal UserSession userSession,
        @PathVariable Long userId,
        @Validated(SignupReq.PasswordUpdate.class) @RequestBody SignupReq request
    ) {
        var response = business.updatePassword(userSession, userId, request);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PatchMapping("{userId}/lang")
    public ResponseEntity<Api<UserInfoRes>> updateLang(
        @AuthenticationPrincipal UserSession userSession,
        @PathVariable Long userId,
        @Validated(UpdateLangReq.UpdateLang.class) @RequestBody UpdateLangReq request
    ) {
        var response = business.updateLang(userSession, userId, request);
        return ResponseEntity.ok(Api.OK(response));
    }

    @PatchMapping("{userId}/info")
    public ResponseEntity<Api<UserInfoRes>> updateInfo(
        @AuthenticationPrincipal UserSession userSession,
        @PathVariable Long userId,
        @Validated @RequestBody EmployeeReq request
    ) {
        var response = business.updateInfo(userSession, userId, request);
        return ResponseEntity.ok(Api.OK(response));
    }
}
