package com.djccnt15.northwind.domain.product.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductCategoryErrorConst {

    public static final String NOT_FOUND_ERR_MSG = "error.productCategory.notFound";
    public static final String NAME_DUPLICATE_ERR_MSG = "error.productCategory.nameDuplicate";
    public static final String CODE_DUPLICATE_ERR_MSG = "error.productCategory.codeDuplicate";
    public static final String HAS_PRODUCTS_ERR_MSG = "error.productCategory.hasProducts";
}
