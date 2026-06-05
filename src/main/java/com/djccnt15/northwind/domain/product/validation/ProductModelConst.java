package com.djccnt15.northwind.domain.product.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductModelConst {

    public static final int CODE_MIN_LENGTH = 1;
    public static final int CODE_MAX_LENGTH = 50;
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 255;

    public static final String CODE_NOT_BLANK_MSG = "Product code must not be blank";
    public static final String CODE_LENGTH_MSG = "Product code must be between " + CODE_MIN_LENGTH + " and " + CODE_MAX_LENGTH + " characters long";
    public static final String NAME_NOT_BLANK_MSG = "Product name must not be blank";
    public static final String NAME_LENGTH_MSG = "Product name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters long";
    public static final String COST_NOT_NULL_MSG = "Standard unit cost must not be null";
    public static final String PRICE_NOT_NULL_MSG = "Unit price must not be null";
    public static final String REORDER_NOT_NULL_MSG = "Reorder level must not be null";
    public static final String TARGET_NOT_NULL_MSG = "Target level must not be null";
    public static final String QPU_NOT_NULL_MSG = "Quantity per unit must not be null";
    public static final String MRQ_NOT_NULL_MSG = "Minimum reorder quantity must not be null";
    public static final String DISCONTINUED_NOT_NULL_MSG = "Discontinued must not be null";
    public static final String CATEGORY_NOT_NULL_MSG = "Category must not be null";
}
