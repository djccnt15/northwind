package com.djccnt15.northwind.domain.order.converter;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.domain.order.model.CompanyRef;
import com.djccnt15.northwind.domain.order.model.OrderCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailRes;
import com.djccnt15.northwind.domain.order.model.OrderListRes;
import com.djccnt15.northwind.domain.order.model.OrderRes;
import com.djccnt15.northwind.domain.order.model.TaxStatusRef;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Converter
@RequiredArgsConstructor
public class OrderConverter {

    private final OrderDetailConverter orderDetailConverter;
    private final OrderStatusConverter orderStatusConverter;

    public OrderListRes toListResponse(OrdersEntity entity, BigDecimal totalAmount) {
        return OrderListRes.builder()
            .id(entity.getId())
            .orderDate(entity.getOrderDate())
            .customerName(entity.getCustomer().getName())
            .shipperName(Optional.ofNullable(entity.getShipper())
                .map(CompanyEntity::getName)
                .orElse(null))
            .status(orderStatusConverter.toRef(entity.getOrderStatus()))
            .totalAmount(Optional.ofNullable(totalAmount).orElse(BigDecimal.ZERO))
            .build();
    }

    public OrderRes toResponse(OrdersEntity entity) {
        var details = entity.getOrderDetails().stream()
            .sorted(Comparator.comparing(d -> Optional.ofNullable(d.getId()).orElse(0L)))
            .map(orderDetailConverter::toResponse)
            .toList();
        return OrderRes.builder()
            .id(entity.getId())
            .orderDate(entity.getOrderDate())
            .requiredDate(entity.getInvoiceDate())
            .shippedDate(entity.getShippedDate())
            .paidDate(entity.getPaidDate())
            .shippingFee(entity.getShippingFee())
            .taxRate(entity.getTaxRate())
            .paymentType(entity.getPaymentType())
            .notes(entity.getNotes())
            .customer(toCompanyRef(entity.getCustomer()))
            .shipper(Optional.ofNullable(entity.getShipper())
                .map(this::toCompanyRef)
                .orElse(null))
            .taxStatus(Optional.ofNullable(entity.getTaxStatus())
                .map(this::toTaxStatusRef)
                .orElse(null))
            .status(orderStatusConverter.toRef(entity.getOrderStatus()))
            .orderDetails(details)
            .totalAmount(calculateTotal(details, entity.getShippingFee()))
            .build();
    }

    public OrdersEntity toEntity(
        OrderCreateReq request,
        CompanyEntity customer,
        CompanyEntity shipper,
        TaxStatusEntity taxStatus,
        OrderStatusEntity orderStatus,
        AppUserEntity appUser
    ) {
        return OrdersEntity.builder()
            .orderDate(LocalDate.now())
            .invoiceDate(request.getRequiredDate())
            .shippingFee(request.getShippingFee())
            .paymentType(request.getPaymentType())
            .notes(request.getNotes())
            .customer(customer)
            .shipper(shipper)
            .taxStatus(taxStatus)
            .orderStatus(orderStatus)
            .appUser(appUser)
            .build();
    }

    private CompanyRef toCompanyRef(CompanyEntity entity) {
        return CompanyRef.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    private TaxStatusRef toTaxStatusRef(TaxStatusEntity entity) {
        return TaxStatusRef.builder()
            .id(entity.getId())
            .status(entity.getStatus())
            .build();
    }

    private BigDecimal calculateTotal(List<OrderDetailRes> details, Integer shippingFee) {
        var sum = details.stream()
            .map(OrderDetailRes::getSubtotal)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.add(BigDecimal.valueOf(Optional.ofNullable(shippingFee).orElse(0)));
    }
}
