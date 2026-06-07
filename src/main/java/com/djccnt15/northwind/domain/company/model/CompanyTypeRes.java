package com.djccnt15.northwind.domain.company.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyTypeRes {

    private Long id;

    private String companyType;
}
