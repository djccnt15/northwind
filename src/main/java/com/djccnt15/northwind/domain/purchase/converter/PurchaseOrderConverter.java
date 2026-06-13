package com.djccnt15.northwind.domain.purchase.converter;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.domain.purchase.model.CompanyRef;
import com.djccnt15.northwind.domain.purchase.model.EmployeeRef;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderDetailRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderListRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Converter
@RequiredArgsConstructor
public class PurchaseOrderConverter {

    private final PurchaseOrderDetailConverter purchaseOrderDetailConverter;
    private final PurchaseOrderStatusConverter purchaseOrderStatusConverter;

    public PurchaseOrderListRes toListResponse(PurchaseOrderEntity entity, BigDecimal totalAmount) {
        return PurchaseOrderListRes.builder()
            .id(entity.getId())
            .submittedDate(entity.getSubmittedDate())
            .vendorName(Optional.ofNullable(entity.getVendor())
                .map(CompanyEntity::getName)
                .orElse(null))
            .status(purchaseOrderStatusConverter.toRef(entity.getStatus()))
            .totalAmount(Optional.ofNullable(totalAmount).orElse(BigDecimal.ZERO))
            .build();
    }

    public PurchaseOrderRes toResponse(PurchaseOrderEntity entity) {
        var details = entity.getPurchaseOrderDetails().stream()
            .sorted(Comparator.comparing(d -> Optional.ofNullable(d.getId()).orElse(0L)))
            .map(purchaseOrderDetailConverter::toResponse).toList();
        return PurchaseOrderRes.builder()
            .id(entity.getId())
            .submittedDate(entity.getSubmittedDate())
            .approvedDate(entity.getApprovedDate())
            .receivedDate(entity.getReceivedDate())
            .paymentDate(entity.getPaymentDate())
            .shippingFee(entity.getShippingFee())
            .taxAmount(entity.getTaxAmount())
            .paymentAmount(entity.getPaymentAmount())
            .paymentMethod(entity.getPaymentMethod())
            .note(entity.getNote())
            .vendor(Optional.ofNullable(entity.getVendor())
                .map(this::toCompanyRef)
                .orElse(null))
            .submittedBy(Optional.ofNullable(entity.getSubmittedBy())
                .map(this::toEmployeeRef)
                .orElse(null))
            .approvedBy(Optional.ofNullable(entity.getApprovedBy())
                .map(this::toEmployeeRef)
                .orElse(null))
            .status(purchaseOrderStatusConverter.toRef(entity.getStatus()))
            .purchaseOrderDetails(details)
            .totalAmount(calculateTotal(details, entity.getShippingFee()))
            .build();
    }

    public PurchaseOrderEntity toEntity(
        PurchaseOrderCreateReq request,
        CompanyEntity vendor,
        EmployeeEntity submittedBy,
        PurchaseOrderStatusEntity status
    ) {
        return PurchaseOrderEntity.builder()
            .submittedDate(LocalDate.now())
            .shippingFee(request.getShippingFee())
            .taxAmount(request.getTaxAmount())
            .note(request.getNote())
            .vendor(vendor)
            .submittedBy(submittedBy)
            .status(status)
            .build();
    }

    private CompanyRef toCompanyRef(CompanyEntity entity) {
        return CompanyRef.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    private EmployeeRef toEmployeeRef(EmployeeEntity entity) {
        return EmployeeRef.builder()
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .build();
    }

    private BigDecimal calculateTotal(List<PurchaseOrderDetailRes> details, Integer shippingFee) {
        var sum = details.stream()
            .map(PurchaseOrderDetailRes::getSubtotal)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.add(BigDecimal.valueOf(Optional.ofNullable(shippingFee).orElse(0)));
    }
}
