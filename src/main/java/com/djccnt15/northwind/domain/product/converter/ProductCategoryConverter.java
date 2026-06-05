package com.djccnt15.northwind.domain.product.converter;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.domain.product.model.ProductCategoryCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ProductCategoryConverter {

    public ProductCategoryRes toResponse(ProductCategoryEntity entity) {
        return ProductCategoryRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .code(entity.getCode())
            .description(entity.getDescription())
            .build();
    }

    public ProductCategoryEntity toEntity(ProductCategoryCreateReq request) {
        return ProductCategoryEntity.builder()
            .name(request.getName())
            .code(request.getCode())
            .description(request.getDescription())
            .build();
    }
}
