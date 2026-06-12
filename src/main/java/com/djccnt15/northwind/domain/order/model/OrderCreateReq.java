package com.djccnt15.northwind.domain.order.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.djccnt15.northwind.domain.order.validation.OrderModelConst.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateReq {

    @NotNull(message = CUSTOMER_NOT_NULL_MSG)
    private Long customerId;

    private Long shipperId;

    private LocalDate requiredDate;

    @NotNull(message = TAX_STATUS_NOT_NULL_MSG)
    private Long taxStatusId;

    private String paymentType;

    private Integer shippingFee;

    private String notes;

    @Valid
    @NotEmpty(message = DETAILS_NOT_EMPTY_MSG)
    private List<OrderDetailCreateReq> orderDetails;
}
