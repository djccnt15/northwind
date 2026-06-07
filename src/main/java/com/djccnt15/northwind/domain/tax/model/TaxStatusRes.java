package com.djccnt15.northwind.domain.tax.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxStatusRes {

    private Long id;

    private String status;
}
