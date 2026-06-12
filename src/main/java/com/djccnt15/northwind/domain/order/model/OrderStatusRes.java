package com.djccnt15.northwind.domain.order.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusRes {

    private Long id;

    private String code;

    private String name;

    private String sortOrder;
}
