package com.djccnt15.northwind.domain.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderRes {

    private Long id;

    private LocalDate orderDate;

    private LocalDate requiredDate;

    private LocalDate shippedDate;

    private LocalDate paidDate;

    private Integer shippingFee;

    private Integer taxRate;

    private String paymentType;

    private String notes;

    private CompanyRef customer;

    private CompanyRef shipper;

    private TaxStatusRef taxStatus;

    private OrderStatusRef status;

    private List<OrderDetailRes> orderDetails;

    private BigDecimal totalAmount;
}
