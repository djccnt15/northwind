package com.djccnt15.northwind.domain.user.controller;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.domain.user.business.AdminUserBusiness;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.djccnt15.northwind.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/admin/user")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminUserApiController {
    
    private final AdminUserBusiness business;
    
    @GetMapping("/users")
    public ResponseEntity<Api<?>> getAllUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String kw
    ) {
        var response = business.getAllUsers(page, size, kw);
        return ResponseEntity.ok(Api.OK(response));
    }
}
