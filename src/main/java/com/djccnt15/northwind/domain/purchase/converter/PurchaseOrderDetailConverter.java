package com.djccnt15.northwind.domain.purchase.converter;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderDetailEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.domain.purchase.model.ProductRef;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderDetailCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderDetailRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Converter
@RequiredArgsConstructor
public class PurchaseOrderDetailConverter {

    public PurchaseOrderDetailRes toResponse(PurchaseOrderDetailEntity entity) {
        return PurchaseOrderDetailRes.builder()
            .id(entity.getId())
            .product(ProductRef.builder()
                .id(entity.getProduct().getId())
                .name(entity.getProduct().getName())
                .build())
            .unitPrice(entity.getUnitPrice())
            .quantity(entity.getQuantity())
            .subtotal(calculateSubtotal(entity))
            .build();
    }

    public PurchaseOrderDetailEntity toEntity(
        PurchaseOrderDetailCreateReq request,
        ProductEntity product,
        PurchaseOrderEntity purchaseOrder
    ) {
        // default the purchase unit price to the product's standardUnitCost (cost price)
        var unitPrice = Optional.ofNullable(request.getUnitPrice())
            .orElse(product.getStandardUnitCost());
        return PurchaseOrderDetailEntity.builder()
            .quantity(request.getQuantity())
            .unitPrice(unitPrice)
            .product(product)
            .purchaseOrder(purchaseOrder)
            .build();
    }

    /**
     * subtotal = unitPrice * quantity, rounded to 2 decimals (HALF_UP).
     */
    public BigDecimal calculateSubtotal(PurchaseOrderDetailEntity entity) {
        var unitPrice = Optional.ofNullable(entity.getUnitPrice()).orElse(BigDecimal.ZERO);
        var quantity = Optional.ofNullable(entity.getQuantity()).orElse(0);
        return unitPrice
            .multiply(BigDecimal.valueOf(quantity))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
