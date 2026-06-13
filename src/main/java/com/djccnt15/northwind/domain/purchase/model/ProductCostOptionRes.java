package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCostOptionRes {

    private Long id;

    private String name;

    private BigDecimal standardUnitCost;
}
