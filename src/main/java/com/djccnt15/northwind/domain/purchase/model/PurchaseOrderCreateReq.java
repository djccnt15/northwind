package com.djccnt15.northwind.domain.purchase.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderModelConst.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreateReq {

    @NotNull(message = VENDOR_NOT_NULL_MSG)
    private Long vendorId;

    private Integer shippingFee;

    private BigDecimal taxAmount;

    private String note;

    @Valid
    @NotEmpty(message = DETAILS_NOT_EMPTY_MSG)
    private List<PurchaseOrderDetailCreateReq> purchaseOrderDetails;
}
