package com.djccnt15.northwind.domain.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderDetailRes {

    private Long id;

    private ProductRef product;

    private BigDecimal unitPrice;

    private Integer quantity;

    private Integer discount;

    private BigDecimal subtotal;

    private OrderDetailStatusRef status;
}
