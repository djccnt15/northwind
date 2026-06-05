package com.djccnt15.northwind.domain.product.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

import static com.djccnt15.northwind.domain.product.validation.ProductModelConst.*;

@Data
@AllArgsConstructor
public class ProductCreateReq {

    @NotBlank(message = CODE_NOT_BLANK_MSG)
    @Size(min = CODE_MIN_LENGTH, max = CODE_MAX_LENGTH, message = CODE_LENGTH_MSG)
    private String code;

    @NotBlank(message = NAME_NOT_BLANK_MSG)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_LENGTH_MSG)
    private String name;

    private String description;

    @NotNull(message = COST_NOT_NULL_MSG)
    private BigDecimal standardUnitCost;

    @NotNull(message = PRICE_NOT_NULL_MSG)
    private BigDecimal unitPrice;

    @NotNull(message = REORDER_NOT_NULL_MSG)
    private Integer reorderLevel;

    @NotNull(message = TARGET_NOT_NULL_MSG)
    private Integer targetLevel;

    @NotNull(message = QPU_NOT_NULL_MSG)
    private Integer quantityPerUnit;

    @NotNull(message = MRQ_NOT_NULL_MSG)
    private Integer minimumReorderQuantity;

    @NotNull(message = DISCONTINUED_NOT_NULL_MSG)
    private Boolean discontinued;

    @NotNull(message = CATEGORY_NOT_NULL_MSG)
    private Long categoryId;
}
