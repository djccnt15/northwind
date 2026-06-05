package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.domain.product.converter.ProductCategoryConverter;
import com.djccnt15.northwind.domain.product.model.ProductCategoryCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.domain.product.service.ProductCategoryService;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminProductCategoryBusiness {

    private final ProductCategoryService service;
    private final ProductCategoryConverter converter;

    @Transactional(rollbackFor = Exception.class)
    public ProductCategoryRes createCategory(ProductCategoryCreateReq request) {
        service.validateCategory(request);
        var entity = service.createCategory(request);
        return converter.toResponse(entity);
    }

    public Page<ProductCategoryRes> getCategories(int page, int size, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by("name"));
        return service.getCategories(kw, pageable).map(converter::toResponse);
    }

    public List<ProductCategoryRes> getCategories() {
        return service.getCategories().stream().map(converter::toResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductCategoryRes updateCategory(Long id, ProductCategoryCreateReq request) {
        service.validateCategory(id, request);
        var entity = service.getCategory(id);
        service.updateCategory(entity, request);
        return converter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        var entity = service.getCategory(id);
        service.validateCategoryDeletion(entity);
        service.deleteCategory(entity);
    }
}
