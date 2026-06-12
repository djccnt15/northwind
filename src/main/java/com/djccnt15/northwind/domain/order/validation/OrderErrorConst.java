package com.djccnt15.northwind.domain.order.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderErrorConst {

    public static final String NOT_FOUND_ERR_MSG = "error.order.notFound";
    public static final String STATUS_NOT_FOUND_ERR_MSG = "error.orderStatus.notFound";
    public static final String INVALID_STATUS_TRANSITION_ERR_MSG = "error.order.invalidStatusTransition";
}
