package com.djccnt15.northwind.db.projection;

import java.math.BigDecimal;

public interface PurchaseOrderTotalProjection {

    Long getPurchaseOrderId();

    BigDecimal getTotalAmount();
}
