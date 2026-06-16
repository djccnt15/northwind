package com.djccnt15.northwind.domain.lang.converter;

import com.djccnt15.northwind.db.entity.SupportedLangEntity;
import com.djccnt15.northwind.domain.lang.model.LangRes;
import com.djccnt15.northwind.global.annotation.Converter;

@Converter
public class LangConverter {

    public LangRes toResponse(SupportedLangEntity entity) {
        return LangRes.builder()
            .id(entity.getId())
            .lang(entity.getLang())
            .build();
    }
}
