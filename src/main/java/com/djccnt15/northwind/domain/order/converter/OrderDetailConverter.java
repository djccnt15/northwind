package com.djccnt15.northwind.domain.order.converter;

import com.djccnt15.northwind.db.entity.OrderDetailEntity;
import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.domain.order.model.OrderDetailCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailRes;
import com.djccnt15.northwind.domain.order.model.ProductRef;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Converter
@RequiredArgsConstructor
public class OrderDetailConverter {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final OrderDetailStatusConverter orderDetailStatusConverter;

    public OrderDetailRes toResponse(OrderDetailEntity entity) {
        return OrderDetailRes.builder()
            .id(entity.getId())
            .product(ProductRef.builder()
                .id(entity.getProduct().getId())
                .name(entity.getProduct().getName())
                .build())
            .unitPrice(entity.getUnitPrice())
            .quantity(entity.getQuantity())
            .discount(entity.getDiscount())
            .subtotal(calculateSubtotal(entity))
            .status(orderDetailStatusConverter.toRef(entity.getOrderDetailStatus()))
            .build();
    }

    public OrderDetailEntity toEntity(
        OrderDetailCreateReq request,
        ProductEntity product,
        OrderDetailStatusEntity status,
        OrdersEntity order
    ) {
        return OrderDetailEntity.builder()
            .quantity(request.getQuantity())
            .discount(Optional.ofNullable(request.getDiscount()).orElse(0))
            .unitPrice(product.getUnitPrice())
            .standardUnitCost(product.getStandardUnitCost())
            .product(product)
            .orderDetailStatus(status)
            .order(order)
            .build();
    }

    /**
     * subtotal = unitPrice * quantity * (1 - discount / 100), rounded to 2 decimals (HALF_UP).
     */
    public BigDecimal calculateSubtotal(OrderDetailEntity entity) {
        var unitPrice = Optional.ofNullable(entity.getUnitPrice()).orElse(BigDecimal.ZERO);
        var quantity = Optional.ofNullable(entity.getQuantity()).orElse(0);
        var discount = Optional.ofNullable(entity.getDiscount()).orElse(0);
        var discountFactor = HUNDRED.subtract(BigDecimal.valueOf(discount))
            .divide(HUNDRED);
        return unitPrice
            .multiply(BigDecimal.valueOf(quantity))
            .multiply(discountFactor)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
