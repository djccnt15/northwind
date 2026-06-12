package com.djccnt15.northwind.domain.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class OrderListRes {

    private Long id;

    private LocalDate orderDate;

    private String customerName;

    private String shipperName;

    private OrderStatusRef status;

    private BigDecimal totalAmount;
}
