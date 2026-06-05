package com.djccnt15.northwind.domain.product.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategoryRes {

    private Long id;

    private String name;

    private String code;

    private String description;
}
