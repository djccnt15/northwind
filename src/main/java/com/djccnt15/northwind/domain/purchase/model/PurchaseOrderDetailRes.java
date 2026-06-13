package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PurchaseOrderDetailRes {

    private Long id;

    private ProductRef product;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;
}
