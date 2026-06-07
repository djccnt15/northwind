package com.djccnt15.northwind.domain.company.model;

import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRes {

    private Long id;

    private String name;

    private String businessPhone;

    private String website;

    private String notes;

    private String address;

    private String city;

    private String region;

    private String zipCode;

    private String country;

    private CompanyTypeRes companyType;

    private TaxStatusRes taxStatus;
}
