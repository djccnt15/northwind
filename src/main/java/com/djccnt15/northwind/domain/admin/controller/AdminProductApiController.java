package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminProductBusiness;
import com.djccnt15.northwind.domain.product.model.ProductCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductRes;
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
@RequestMapping(API_V1 + "/admin/products")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminProductApiController {

    private final AdminProductBusiness business;

    @PostMapping
    public ResponseEntity<Api<ProductRes>> createProduct(
        @Validated @RequestBody ProductCreateReq request
    ) {
        var response = business.createProduct(request);
        return ResponseEntity.ok(Api.CREATED(response));
    }

    @PutMapping("{id}")
    public ResponseEntity<Api<ProductRes>> updateProduct(
        @PathVariable Long id,
        @Validated @RequestBody ProductCreateReq request
    ) {
        var response = business.updateProduct(id, request);
        return ResponseEntity.ok(Api.OK(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Api<?>> discontinueProduct(@PathVariable Long id) {
        business.discontinueProduct(id);
        return ResponseEntity.ok(Api.OK(null));
    }
}
