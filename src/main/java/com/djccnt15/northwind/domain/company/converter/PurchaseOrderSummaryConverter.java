package com.djccnt15.northwind.domain.company.converter;

import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.domain.company.model.PurchaseOrderSummaryRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class PurchaseOrderSummaryConverter {

    public PurchaseOrderSummaryRes toResponse(PurchaseOrderEntity entity) {
        return PurchaseOrderSummaryRes.builder()
            .id(entity.getId())
            .submittedDate(entity.getSubmittedDate())
            .approvedDate(entity.getApprovedDate())
            .receivedDate(entity.getReceivedDate())
            .paymentAmount(entity.getPaymentAmount())
            .status(Optional.ofNullable(entity.getStatus())
                .map(PurchaseOrderStatusEntity::getName)
                .orElse(null))
            .build();
    }
}
