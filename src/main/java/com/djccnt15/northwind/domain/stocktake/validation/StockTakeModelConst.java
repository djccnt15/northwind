package com.djccnt15.northwind.domain.stocktake.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StockTakeModelConst {

    public static final String DATE_NOT_NULL_MSG = "{validation.stockTake.date.notNull}";
    public static final String ITEMS_NOT_EMPTY_MSG = "{validation.stockTake.items.notEmpty}";
    public static final String PRODUCT_NOT_NULL_MSG = "{validation.stockTake.product.notNull}";
    public static final String QUANTITY_NOT_NULL_MSG = "{validation.stockTake.quantity.notNull}";
    public static final String QUANTITY_MIN_MSG = "{validation.stockTake.quantity.min}";
}
