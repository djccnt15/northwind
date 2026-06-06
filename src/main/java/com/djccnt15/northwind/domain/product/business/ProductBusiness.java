package com.djccnt15.northwind.domain.product.business;

import com.djccnt15.northwind.domain.product.converter.ProductCategoryConverter;
import com.djccnt15.northwind.domain.product.converter.ProductConverter;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.domain.product.model.ProductCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductRes;
import com.djccnt15.northwind.domain.product.service.ProductCategoryService;
import com.djccnt15.northwind.domain.product.service.ProductService;
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
public class ProductBusiness {

    private final ProductService service;
    private final ProductConverter converter;
    private final ProductCategoryService categoryService;
    private final ProductCategoryConverter categoryConverter;

    public Page<ProductRes> getProducts(
        int page, int size, String keyword, Long categoryId, Boolean discontinued
    ) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by("name"));
        return service.getProducts(kw, categoryId, discontinued, pageable).map(converter::toResponse);
    }

    public ProductRes getProduct(Long id) {
        return converter.toResponse(service.getProduct(id));
    }

    public List<ProductCategoryRes> getCategories() {
        return categoryService.getCategories().stream().map(categoryConverter::toResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductRes createProduct(ProductCreateReq request) {
        service.validateProduct(request);
        var category = categoryService.getCategory(request.getCategoryId());
        var entity = service.createProduct(request, category);
        return converter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductRes updateProduct(Long id, ProductCreateReq request) {
        service.validateProduct(id, request);
        var entity = service.getProduct(id);
        var category = categoryService.getCategory(request.getCategoryId());
        service.updateProduct(entity, request, category);
        return converter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void discontinueProduct(Long id) {
        var entity = service.getProduct(id);
        service.discontinueProduct(entity);
    }
}
