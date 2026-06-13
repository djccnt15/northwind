package com.djccnt15.northwind.domain.purchase.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderModelConst.STATUS_NOT_NULL_MSG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderStatusUpdateReq {

    @NotNull(message = STATUS_NOT_NULL_MSG)
    private Long statusId;

    // optional payment details, recorded only on transition to PAID
    private LocalDate paymentDate;

    private Integer paymentAmount;

    private String paymentMethod;
}
