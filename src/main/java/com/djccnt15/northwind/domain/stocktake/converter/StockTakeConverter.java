package com.djccnt15.northwind.domain.stocktake.converter;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeRowRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class StockTakeConverter {

    /**
     * Builds a grid row from a product, its computed expected quantity (latest stock-take),
     * and today's draft quantity-on-hand (nullable when no draft exists yet).
     */
    public StockTakeRowRes toRowResponse(
        ProductEntity product,
        Long expectedQuantity,
        Long draftQuantityOnHand
    ) {
        return StockTakeRowRes.builder()
            .productId(product.getId())
            .productCode(product.getCode())
            .productName(product.getName())
            .expectedQuantity(expectedQuantity)
            .quantityOnHand(draftQuantityOnHand)
            .build();
    }
}
