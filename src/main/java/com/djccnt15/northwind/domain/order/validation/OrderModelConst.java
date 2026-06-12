package com.djccnt15.northwind.domain.order.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderModelConst {

    public static final int PAYMENT_TYPE_MAX_LENGTH = 50;

    public static final String CUSTOMER_NOT_NULL_MSG = "{validation.order.customer.notNull}";
    public static final String TAX_STATUS_NOT_NULL_MSG = "{validation.order.taxStatus.notNull}";
    public static final String DETAILS_NOT_EMPTY_MSG = "{validation.order.orderDetails.notEmpty}";
    public static final String STATUS_NOT_NULL_MSG = "{validation.order.status.notNull}";
}
