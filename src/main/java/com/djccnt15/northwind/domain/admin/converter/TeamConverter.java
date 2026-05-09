package com.djccnt15.northwind.domain.admin.converter;

import com.djccnt15.northwind.db.entity.TeamEntity;
import com.djccnt15.northwind.domain.admin.model.TeamCreateReq;
import com.djccnt15.northwind.domain.admin.model.TeamRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class TeamConverter {
    
    public TeamRes toResponse(TeamEntity entity) {
        return TeamRes.builder()
            .name(entity.getName())
            .build();
    }
    
    public TeamEntity toEntity(TeamCreateReq request) {
        return TeamEntity.builder()
            .name(request.getName())
            .build();
    }
}
