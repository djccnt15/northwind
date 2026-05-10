package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminUserBusiness;
import com.djccnt15.northwind.domain.model.ListBodyReq;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/admin/user")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminUserApiController {
    
    private final AdminUserBusiness business;
    
    @GetMapping("/all")
    public ResponseEntity<Api<Page<UserInfoRes>>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String keyword
    ) {
        var response = business.getUsers(page, size, keyword);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PatchMapping("{userId}/profile")
    public ResponseEntity<Api<UserInfoRes>> updateUser(
        @PathVariable Long userId,
        @Validated(SignupReq.AdminUpdate.class) @RequestBody SignupReq req
    ) {
        var response = business.updateUser(userId, req);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PatchMapping("{userId}/reset-password")
    public ResponseEntity<Api<UserInfoRes>> resetPassword(@PathVariable Long userId) {
        var response = business.resetPassword(userId);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PatchMapping("{userId}/roles")
    public ResponseEntity<Api<UserInfoRes>> updateUserRoles(
        @PathVariable Long userId,
        @RequestBody ListBodyReq<String> request
    ) {
        var response = business.updateUserRoles(userId, request);
        return ResponseEntity.ok(Api.OK(response));
    }
}
