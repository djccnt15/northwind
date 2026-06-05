package com.djccnt15.northwind.domain.product.controller;

import com.djccnt15.northwind.domain.product.business.ProductBusiness;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.domain.product.model.ProductRes;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductBusiness business;

    @GetMapping("/products")
    public ResponseEntity<Api<Page<ProductRes>>> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Boolean discontinued
    ) {
        var response = business.getProducts(page, size, keyword, categoryId, discontinued);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Api<ProductRes>> getProduct(@PathVariable Long id) {
        var response = business.getProduct(id);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/categories/all")
    public ResponseEntity<Api<List<ProductCategoryRes>>> getAllCategories() {
        var response = business.getCategories();
        return ResponseEntity.ok(Api.OK(response));
    }
}
