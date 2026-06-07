package com.djccnt15.northwind.domain.company.converter;

import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class CompanyTypeConverter {

    public CompanyTypeRes toResponse(CompanyTypeEntity entity) {
        return CompanyTypeRes.builder()
            .id(entity.getId())
            .companyType(entity.getCompanyType())
            .build();
    }
}
