package com.djccnt15.northwind.db.projection;

public interface StockTakeLatestProjection {

    Long getProductId();

    Long getQuantityOnHand();
}
