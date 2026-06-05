package com.djccnt15.northwind.domain.product.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductCategoryModelConst {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    public static final int CODE_MIN_LENGTH = 1;
    public static final int CODE_MAX_LENGTH = 20;

    public static final String NAME_NOT_BLANK_MSG = "Category name must not be blank";
    public static final String NAME_LENGTH_MSG = "Category name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters long";
    public static final String CODE_NOT_BLANK_MSG = "Category code must not be blank";
    public static final String CODE_LENGTH_MSG = "Category code must be between " + CODE_MIN_LENGTH + " and " + CODE_MAX_LENGTH + " characters long";
}
