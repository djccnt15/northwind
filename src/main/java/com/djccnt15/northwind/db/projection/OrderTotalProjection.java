package com.djccnt15.northwind.db.projection;

import java.math.BigDecimal;

public interface OrderTotalProjection {
    
    Long getOrderId();
    
    BigDecimal getTotalAmount();
}
