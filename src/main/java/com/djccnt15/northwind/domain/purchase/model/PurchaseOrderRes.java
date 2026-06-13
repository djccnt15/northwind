package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PurchaseOrderRes {

    private Long id;

    private LocalDate submittedDate;

    private LocalDate approvedDate;

    private LocalDate receivedDate;

    private LocalDate paymentDate;

    private Integer shippingFee;

    private BigDecimal taxAmount;

    private Integer paymentAmount;

    private String paymentMethod;

    private String note;

    private CompanyRef vendor;

    private EmployeeRef submittedBy;

    private EmployeeRef approvedBy;

    private PurchaseOrderStatusRef status;

    private List<PurchaseOrderDetailRes> purchaseOrderDetails;

    private BigDecimal totalAmount;
}
