package com.djccnt15.northwind.domain.order.converter;

import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusRef;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class OrderDetailStatusConverter {

    public OrderDetailStatusRes toResponse(OrderDetailStatusEntity entity) {
        return OrderDetailStatusRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .sortOrder(Optional.ofNullable(entity.getSortOrder()).map(Enum::name).orElse(null))
            .build();
    }

    public OrderDetailStatusRef toRef(OrderDetailStatusEntity entity) {
        return OrderDetailStatusRef.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }
}
