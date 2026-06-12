package com.djccnt15.northwind.domain.order.converter;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.domain.order.model.ProductOptionRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ProductOptionConverter {

    public ProductOptionRes toResponse(ProductEntity entity) {
        return ProductOptionRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .unitPrice(entity.getUnitPrice())
            .build();
    }
}
