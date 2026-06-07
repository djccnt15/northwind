package com.djccnt15.northwind.domain.company.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CompanyModelConst {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    public static final int BUSINESS_PHONE_MAX_LENGTH = 25;
    public static final int WEBSITE_MAX_LENGTH = 100;

    public static final String NAME_NOT_BLANK_MSG = "Company name must not be blank";
    public static final String NAME_LENGTH_MSG =
        "Company name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters long";
    public static final String BUSINESS_PHONE_LENGTH_MSG =
        "Business phone must be at most " + BUSINESS_PHONE_MAX_LENGTH + " characters long";
    public static final String WEBSITE_LENGTH_MSG =
        "Website must be at most " + WEBSITE_MAX_LENGTH + " characters long";

    public static final String COMPANY_TYPE_NOT_NULL_MSG = "Company type must not be null";
    public static final String TAX_STATUS_NOT_NULL_MSG = "Tax status must not be null";
}
