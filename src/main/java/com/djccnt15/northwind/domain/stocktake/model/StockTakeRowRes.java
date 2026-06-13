package com.djccnt15.northwind.domain.stocktake.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockTakeRowRes {

    private Long productId;

    private String productCode;

    private String productName;

    /** Computer stock = latest stock-take quantity-on-hand (0 when never counted). */
    private Long expectedQuantity;

    /** Today's draft count, null when no draft has been saved today yet. */
    private Long quantityOnHand;
}
