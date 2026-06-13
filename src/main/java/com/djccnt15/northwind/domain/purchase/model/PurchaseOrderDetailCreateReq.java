package com.djccnt15.northwind.domain.purchase.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderDetailModelConst.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetailCreateReq {

    @NotNull(message = PRODUCT_NOT_NULL_MSG)
    private Long productId;

    @NotNull(message = QUANTITY_NOT_NULL_MSG)
    @Min(value = QUANTITY_MIN, message = QUANTITY_MIN_MSG)
    private Integer quantity;

    /**
     * Optional override of the purchase unit price. When omitted the product's
     * standardUnitCost (cost price) is used as the default.
     */
    private BigDecimal unitPrice;
}
