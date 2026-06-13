package com.djccnt15.northwind.domain.purchase.converter;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.domain.purchase.model.CompanyOptionRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class VendorOptionConverter {

    public CompanyOptionRes toResponse(CompanyEntity entity) {
        return CompanyOptionRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }
}
