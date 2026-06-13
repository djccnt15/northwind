package com.djccnt15.northwind.domain.purchase.converter;

import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusRef;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class PurchaseOrderStatusConverter {

    public PurchaseOrderStatusRes toResponse(PurchaseOrderStatusEntity entity) {
        return PurchaseOrderStatusRes.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .sortOrder(Optional.ofNullable(entity.getSortOrder())
                .map(Enum::name)
                .orElse(null))
            .build();
    }

    public PurchaseOrderStatusRef toRef(PurchaseOrderStatusEntity entity) {
        return PurchaseOrderStatusRef.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .build();
    }
}
