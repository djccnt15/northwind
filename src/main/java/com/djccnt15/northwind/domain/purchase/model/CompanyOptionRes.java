package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyOptionRes {

    private Long id;

    private String name;
}
