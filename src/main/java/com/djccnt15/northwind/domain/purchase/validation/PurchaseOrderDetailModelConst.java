package com.djccnt15.northwind.domain.purchase.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PurchaseOrderDetailModelConst {

    public static final int QUANTITY_MIN = 1;

    public static final String PRODUCT_NOT_NULL_MSG = "{validation.purchaseOrderDetail.product.notNull}";
    public static final String QUANTITY_NOT_NULL_MSG = "{validation.purchaseOrderDetail.quantity.notNull}";
    public static final String QUANTITY_MIN_MSG = "{validation.purchaseOrderDetail.quantity.min}";
}
