package com.djccnt15.northwind.domain.company.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.djccnt15.northwind.domain.company.validation.CompanyModelConst.*;

@Data
@AllArgsConstructor
public class CompanyCreateReq {

    @NotBlank(message = NAME_NOT_BLANK_MSG)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_LENGTH_MSG)
    private String name;

    @Size(max = BUSINESS_PHONE_MAX_LENGTH, message = BUSINESS_PHONE_LENGTH_MSG)
    private String businessPhone;

    @Size(max = WEBSITE_MAX_LENGTH, message = WEBSITE_LENGTH_MSG)
    private String website;

    private String notes;

    private String address;

    private String city;

    private String region;

    private String zipCode;

    private String country;

    @NotNull(message = COMPANY_TYPE_NOT_NULL_MSG)
    private Long companyTypeId;

    @NotNull(message = TAX_STATUS_NOT_NULL_MSG)
    private Long taxStatusId;
}
