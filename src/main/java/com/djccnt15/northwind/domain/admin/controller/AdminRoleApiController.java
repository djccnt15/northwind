package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminRoleBusiness;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/admin/role")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminRoleApiController {
    
    private final AdminRoleBusiness business;
    
    @GetMapping("/all")
    public ResponseEntity<Api<List<String>>> getAllRoles() {
        var response = business.getAllRoles();
        return ResponseEntity.ok(Api.OK(response));
    }
}
