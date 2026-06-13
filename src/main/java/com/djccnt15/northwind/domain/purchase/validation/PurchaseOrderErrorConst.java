package com.djccnt15.northwind.domain.purchase.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PurchaseOrderErrorConst {

    public static final String NOT_FOUND_ERR_MSG = "error.purchaseOrder.notFound";
    public static final String STATUS_NOT_FOUND_ERR_MSG = "error.purchaseOrderStatus.notFound";
    public static final String INVALID_STATUS_TRANSITION_ERR_MSG = "error.purchaseOrder.invalidStatusTransition";
    public static final String EMPLOYEE_REQUIRED_ERR_MSG = "error.purchaseOrder.employeeRequired";
}
