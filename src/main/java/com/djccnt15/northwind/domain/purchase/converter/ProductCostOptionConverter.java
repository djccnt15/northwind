package com.djccnt15.northwind.domain.purchase.converter;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.domain.purchase.model.ProductCostOptionRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ProductCostOptionConverter {

    public ProductCostOptionRes toResponse(ProductEntity entity) {
        return ProductCostOptionRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .standardUnitCost(entity.getStandardUnitCost())
            .build();
    }
}
