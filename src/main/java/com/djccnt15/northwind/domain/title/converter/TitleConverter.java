package com.djccnt15.northwind.domain.title.converter;

import com.djccnt15.northwind.db.entity.TitleEntity;
import com.djccnt15.northwind.domain.title.model.TitleCreateReq;
import com.djccnt15.northwind.domain.title.model.TitleRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class TitleConverter {
    
    public TitleRes toResponse(TitleEntity entity) {
        return TitleRes.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .build();
    }
    
    public TitleEntity toEntity(TitleCreateReq request) {
        return TitleEntity.builder()
            .title(request.getTitle())
            .build();
    }
}
