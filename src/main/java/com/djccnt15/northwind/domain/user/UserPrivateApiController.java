package com.djccnt15.northwind.domain.user;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.config.security.model.UserSession;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/user")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserPrivateApiController {
    
    private final UserBusiness business;
    
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
}
