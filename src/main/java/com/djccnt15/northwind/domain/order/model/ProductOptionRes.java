package com.djccnt15.northwind.domain.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductOptionRes {

    private Long id;

    private String name;

    private BigDecimal unitPrice;
}
