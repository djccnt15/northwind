package com.djccnt15.northwind.domain.order.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.djccnt15.northwind.domain.order.validation.OrderDetailModelConst.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailCreateReq {

    @NotNull(message = PRODUCT_NOT_NULL_MSG)
    private Long productId;

    @NotNull(message = QUANTITY_NOT_NULL_MSG)
    @Min(value = QUANTITY_MIN, message = QUANTITY_MIN_MSG)
    private Integer quantity;

    @Min(value = DISCOUNT_MIN, message = DISCOUNT_RANGE_MSG)
    @Max(value = DISCOUNT_MAX, message = DISCOUNT_RANGE_MSG)
    private Integer discount;
}
