package com.djccnt15.northwind.domain.order.converter;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.domain.order.model.CompanyOptionRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class CompanyOptionConverter {

    public CompanyOptionRes toResponse(CompanyEntity entity) {
        return CompanyOptionRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }
}
