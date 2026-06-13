package com.djccnt15.northwind.domain.purchase.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PurchaseOrderModelConst {

    public static final String VENDOR_NOT_NULL_MSG = "{validation.purchaseOrder.vendor.notNull}";
    public static final String DETAILS_NOT_EMPTY_MSG = "{validation.purchaseOrder.purchaseOrderDetails.notEmpty}";
    public static final String STATUS_NOT_NULL_MSG = "{validation.purchaseOrder.status.notNull}";
}
