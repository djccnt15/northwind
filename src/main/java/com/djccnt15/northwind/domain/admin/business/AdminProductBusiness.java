package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.domain.product.converter.ProductConverter;
import com.djccnt15.northwind.domain.product.model.ProductCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductRes;
import com.djccnt15.northwind.domain.product.service.ProductCategoryService;
import com.djccnt15.northwind.domain.product.service.ProductService;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminProductBusiness {

    private final ProductService productService;
    private final ProductCategoryService categoryService;
    private final ProductConverter converter;

    @Transactional(rollbackFor = Exception.class)
    public ProductRes createProduct(ProductCreateReq request) {
        productService.validateProduct(request);
        var category = categoryService.getCategory(request.getCategoryId());
        var entity = productService.createProduct(request, category);
        return converter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductRes updateProduct(Long id, ProductCreateReq request) {
        productService.validateProduct(id, request);
        var entity = productService.getProduct(id);
        var category = categoryService.getCategory(request.getCategoryId());
        productService.updateProduct(entity, request, category);
        return converter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void discontinueProduct(Long id) {
        var entity = productService.getProduct(id);
        productService.discontinueProduct(entity);
    }
}
