package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminTitleBusiness;
import com.djccnt15.northwind.domain.title.model.TitleCreateReq;
import com.djccnt15.northwind.domain.title.model.TitleRes;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/admin/titles")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminTitleApiController {
    
    private final AdminTitleBusiness business;
    
    @PostMapping
    public ResponseEntity<Api<TitleRes>> createTitle(@Validated @RequestBody TitleCreateReq request) {
        var response = business.createTitle(request);
        return ResponseEntity.ok(Api.CREATED(response));
    }
    
    @GetMapping
    public ResponseEntity<Api<List<String>>> getTitles() {
        var response = business.getAllTitles();
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @PutMapping("{id}")
    public ResponseEntity<Api<TitleRes>> updateTitle(
        @PathVariable Long id,
        @Validated @RequestBody TitleCreateReq request
    ) {
        var response = business.updateTitle(id, request);
        return ResponseEntity.ok(Api.OK(response));
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<Api<?>> deleteTitle(@PathVariable Long id) {
        business.deleteTitle(id);
        return ResponseEntity.ok(Api.OK(null));
    }
}
