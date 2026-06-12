package com.djccnt15.northwind.domain.order.converter;

import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.domain.order.model.OrderStatusRef;
import com.djccnt15.northwind.domain.order.model.OrderStatusRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class OrderStatusConverter {

    public OrderStatusRes toResponse(OrderStatusEntity entity) {
        return OrderStatusRes.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .sortOrder(Optional.ofNullable(entity.getSortOrder()).map(Enum::name).orElse(null))
            .build();
    }

    public OrderStatusRef toRef(OrderStatusEntity entity) {
        return OrderStatusRef.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .name(entity.getName())
            .build();
    }
}
