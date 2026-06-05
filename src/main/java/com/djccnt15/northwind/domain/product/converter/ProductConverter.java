package com.djccnt15.northwind.domain.product.converter;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.domain.product.model.ProductCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
@RequiredArgsConstructor
public class ProductConverter {

    private final ProductCategoryConverter categoryConverter;

    public ProductRes toResponse(ProductEntity entity) {
        return ProductRes.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .description(entity.getDescription())
            .standardUnitCost(entity.getStandardUnitCost())
            .unitPrice(entity.getUnitPrice())
            .reorderLevel(entity.getReorderLevel())
            .targetLevel(entity.getTargetLevel())
            .quantityPerUnit(entity.getQuantityPerUnit())
            .minimumReorderQuantity(entity.getMinimumReorderQuantity())
            .discontinued(entity.getDiscontinued())
            .category(categoryConverter.toResponse(entity.getProductCategory()))
            .build();
    }

    public ProductEntity toEntity(ProductCreateReq request, ProductCategoryEntity category) {
        return ProductEntity.builder()
            .code(request.getCode())
            .name(request.getName())
            .description(request.getDescription())
            .standardUnitCost(request.getStandardUnitCost())
            .unitPrice(request.getUnitPrice())
            .reorderLevel(request.getReorderLevel())
            .targetLevel(request.getTargetLevel())
            .quantityPerUnit(request.getQuantityPerUnit())
            .minimumReorderQuantity(request.getMinimumReorderQuantity())
            .discontinued(request.getDiscontinued())
            .productCategory(category)
            .build();
    }
}
