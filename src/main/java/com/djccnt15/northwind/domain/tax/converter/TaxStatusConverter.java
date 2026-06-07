package com.djccnt15.northwind.domain.tax.converter;

import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class TaxStatusConverter {

    public TaxStatusRes toResponse(TaxStatusEntity entity) {
        return TaxStatusRes.builder()
            .id(entity.getId())
            .status(entity.getStatus())
            .build();
    }
}
