package com.djccnt15.northwind.domain.stocktake.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.djccnt15.northwind.domain.stocktake.validation.StockTakeModelConst.PRODUCT_NOT_NULL_MSG;
import static com.djccnt15.northwind.domain.stocktake.validation.StockTakeModelConst.QUANTITY_NOT_NULL_MSG;
import static com.djccnt15.northwind.domain.stocktake.validation.StockTakeModelConst.QUANTITY_MIN_MSG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTakeItemReq {

    @NotNull(message = PRODUCT_NOT_NULL_MSG)
    private Long productId;

    @NotNull(message = QUANTITY_NOT_NULL_MSG)
    @PositiveOrZero(message = QUANTITY_MIN_MSG)
    private Long quantityOnHand;
}
