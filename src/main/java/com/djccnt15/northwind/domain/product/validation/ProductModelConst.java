package com.djccnt15.northwind.domain.product.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductModelConst {

    public static final int CODE_MIN_LENGTH = 1;
    public static final int CODE_MAX_LENGTH = 50;
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 255;

    public static final String CODE_NOT_BLANK_MSG = "{validation.product.code.notBlank}";
    public static final String CODE_LENGTH_MSG = "{validation.product.code.size}";
    public static final String NAME_NOT_BLANK_MSG = "{validation.product.name.notBlank}";
    public static final String NAME_LENGTH_MSG = "{validation.product.name.size}";
    public static final String COST_NOT_NULL_MSG = "{validation.product.standardUnitCost.notNull}";
    public static final String PRICE_NOT_NULL_MSG = "{validation.product.unitPrice.notNull}";
    public static final String REORDER_NOT_NULL_MSG = "{validation.product.reorderLevel.notNull}";
    public static final String TARGET_NOT_NULL_MSG = "{validation.product.targetLevel.notNull}";
    public static final String QPU_NOT_NULL_MSG = "{validation.product.quantityPerUnit.notNull}";
    public static final String MRQ_NOT_NULL_MSG = "{validation.product.minimumReorderQuantity.notNull}";
    public static final String DISCONTINUED_NOT_NULL_MSG = "{validation.product.discontinued.notNull}";
    public static final String CATEGORY_NOT_NULL_MSG = "{validation.product.category.notNull}";
}
