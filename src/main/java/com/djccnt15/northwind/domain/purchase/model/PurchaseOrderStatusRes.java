package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseOrderStatusRes {

    private Long id;

    private String code;

    private String name;

    private String sortOrder;
}
