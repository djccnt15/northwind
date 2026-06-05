package com.djccnt15.northwind.domain.product.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.djccnt15.northwind.domain.product.validation.ProductCategoryModelConst.*;

@Data
@AllArgsConstructor
public class ProductCategoryCreateReq {

    @NotBlank(message = NAME_NOT_BLANK_MSG)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_LENGTH_MSG)
    private String name;

    @NotBlank(message = CODE_NOT_BLANK_MSG)
    @Size(min = CODE_MIN_LENGTH, max = CODE_MAX_LENGTH, message = CODE_LENGTH_MSG)
    private String code;

    private String description;
}
