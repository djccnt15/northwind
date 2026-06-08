package com.djccnt15.northwind.domain.company.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CompanyModelConst {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    public static final int BUSINESS_PHONE_MAX_LENGTH = 25;
    public static final int WEBSITE_MAX_LENGTH = 100;

    public static final String NAME_NOT_BLANK_MSG = "{validation.company.name.notBlank}";
    public static final String NAME_LENGTH_MSG = "{validation.company.name.size}";
    public static final String BUSINESS_PHONE_LENGTH_MSG = "{validation.company.businessPhone.size}";
    public static final String WEBSITE_LENGTH_MSG = "{validation.company.website.size}";

    public static final String COMPANY_TYPE_NOT_NULL_MSG = "{validation.company.companyType.notNull}";
    public static final String TAX_STATUS_NOT_NULL_MSG = "{validation.company.taxStatus.notNull}";
}
