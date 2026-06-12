package com.djccnt15.northwind.domain.order.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderDetailModelConst {

    public static final int QUANTITY_MIN = 1;
    public static final int DISCOUNT_MIN = 0;
    public static final int DISCOUNT_MAX = 100;

    public static final String PRODUCT_NOT_NULL_MSG = "{validation.orderDetail.product.notNull}";
    public static final String QUANTITY_NOT_NULL_MSG = "{validation.orderDetail.quantity.notNull}";
    public static final String QUANTITY_MIN_MSG = "{validation.orderDetail.quantity.min}";
    public static final String DISCOUNT_RANGE_MSG = "{validation.orderDetail.discount.range}";
}
