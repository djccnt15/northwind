package com.djccnt15.northwind.domain.company.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PurchaseOrderSummaryRes {

    private Long id;

    private LocalDate submittedDate;

    private LocalDate approvedDate;

    private LocalDate receivedDate;

    private Integer paymentAmount;

    private String status;
}
