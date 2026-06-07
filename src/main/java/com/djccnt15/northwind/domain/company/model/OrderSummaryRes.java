package com.djccnt15.northwind.domain.company.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrderSummaryRes {

    private Long id;

    private LocalDate orderDate;

    private LocalDate shippedDate;

    private LocalDate paidDate;

    private Integer shippingFee;

    private Integer taxRate;

    private String status;
}
