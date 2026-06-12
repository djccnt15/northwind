package com.djccnt15.northwind.domain.order.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRef {

    private Long id;

    private String name;
}
