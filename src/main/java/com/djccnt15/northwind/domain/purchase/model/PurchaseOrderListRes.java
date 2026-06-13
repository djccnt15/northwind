package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PurchaseOrderListRes {

    private Long id;

    private LocalDate submittedDate;

    private String vendorName;

    private PurchaseOrderStatusRef status;

    private BigDecimal totalAmount;
}
