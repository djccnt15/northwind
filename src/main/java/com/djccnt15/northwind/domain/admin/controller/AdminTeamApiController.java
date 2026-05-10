package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminTeamBusiness;
import com.djccnt15.northwind.domain.team.model.TeamCreateReq;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/admin/teams")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminTeamApiController {
    
    private final AdminTeamBusiness business;
    
    @PostMapping
    public ResponseEntity<Api<?>> createTeam(@Validated @RequestBody TeamCreateReq request) {
        var response = business.createTeam(request);
        return ResponseEntity.ok(Api.CREATED(response));
    }
    
    @GetMapping("/all")
    public ResponseEntity<Api<?>> getAllTeams(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String keyword) {
        var response = business.getTeams(page, size, keyword);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Api<?>> updateTeam(
        @PathVariable Long id,
        @Validated @RequestBody TeamCreateReq request
    ) {
        var response = business.updateTeam(id, request);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<Api<?>> deleteTeam(@PathVariable Long id) {
        business.deleteTeam(id);
        return ResponseEntity.ok(Api.OK(null));
    }
}
