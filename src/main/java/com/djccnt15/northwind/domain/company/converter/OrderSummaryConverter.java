package com.djccnt15.northwind.domain.company.converter;

import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.domain.company.model.OrderSummaryRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class OrderSummaryConverter {

    public OrderSummaryRes toResponse(OrdersEntity entity) {
        return OrderSummaryRes.builder()
            .id(entity.getId())
            .orderDate(entity.getOrderDate())
            .shippedDate(entity.getShippedDate())
            .paidDate(entity.getPaidDate())
            .shippingFee(entity.getShippingFee())
            .taxRate(entity.getTaxRate())
            .status(Optional.ofNullable(entity.getOrderStatus())
                .map(s -> s.getName())
                .orElse(null))
            .build();
    }
}
