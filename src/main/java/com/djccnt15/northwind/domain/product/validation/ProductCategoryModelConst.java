package com.djccnt15.northwind.domain.product.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductCategoryModelConst {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    public static final int CODE_MIN_LENGTH = 1;
    public static final int CODE_MAX_LENGTH = 20;

    public static final String NAME_NOT_BLANK_MSG = "{validation.productCategory.name.notBlank}";
    public static final String NAME_LENGTH_MSG = "{validation.productCategory.name.size}";
    public static final String CODE_NOT_BLANK_MSG = "{validation.productCategory.code.notBlank}";
    public static final String CODE_LENGTH_MSG = "{validation.productCategory.code.size}";
}
