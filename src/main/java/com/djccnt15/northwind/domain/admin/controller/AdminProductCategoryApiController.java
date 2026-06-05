package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminProductCategoryBusiness;
import com.djccnt15.northwind.domain.product.model.ProductCategoryCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/admin/categories")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminProductCategoryApiController {

    private final AdminProductCategoryBusiness business;

    @PostMapping
    public ResponseEntity<Api<ProductCategoryRes>> createCategory(
        @Validated @RequestBody ProductCategoryCreateReq request
    ) {
        var response = business.createCategory(request);
        return ResponseEntity.ok(Api.CREATED(response));
    }

    @GetMapping
    public ResponseEntity<Api<Page<ProductCategoryRes>>> getCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String keyword
    ) {
        var response = business.getCategories(page, size, keyword);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/all")
    public ResponseEntity<Api<List<ProductCategoryRes>>> getAllCategories() {
        var response = business.getCategories();
        return ResponseEntity.ok(Api.OK(response));
    }

    @PutMapping("{id}")
    public ResponseEntity<Api<ProductCategoryRes>> updateCategory(
        @PathVariable Long id,
        @Validated @RequestBody ProductCategoryCreateReq request
    ) {
        var response = business.updateCategory(id, request);
        return ResponseEntity.ok(Api.OK(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Api<?>> deleteCategory(@PathVariable Long id) {
        business.deleteCategory(id);
        return ResponseEntity.ok(Api.OK(null));
    }
}
