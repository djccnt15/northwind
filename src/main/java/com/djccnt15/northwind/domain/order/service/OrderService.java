package com.djccnt15.northwind.domain.order.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.OrdersRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.djccnt15.northwind.domain.order.validation.OrderErrorConst.INVALID_STATUS_TRANSITION_ERR_MSG;
import static com.djccnt15.northwind.domain.order.validation.OrderErrorConst.NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    public static final String CODE_PENDING = "PENDING";
    public static final String CODE_PAID = "PAID";
    public static final String CODE_SHIPPED = "SHIPPED";
    public static final String CODE_DELIVERED = "DELIVERED";
    public static final String CODE_CANCELLED = "CANCELLED";

    // forward progression rank by status code; CANCELLED handled separately
    private static final Map<String, Integer> STATUS_RANK = Map.of(
        CODE_PENDING, 1,
        CODE_PAID, 2,
        CODE_SHIPPED, 3,
        CODE_DELIVERED, 4
    );

    private final OrdersRepo repository;
    private final AppUserRepo appUserRepo;
    private final MessageUtil messageUtil;

    public Page<OrdersEntity> getOrders(
        String kw, Long statusId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable
    ) {
        return repository.findByFilter(kw, statusId, dateFrom, dateTo, pageable);
    }

    public Map<Long, BigDecimal> getTotalAmounts(List<Long> orderIds) {
        if (orderIds.isEmpty()) {
            return Map.of();
        }
        return repository.findTotalAmountByIdIn(orderIds).stream()
            .collect(Collectors.toMap(
                OrdersRepo.OrderTotalProjection::getOrderId,
                OrdersRepo.OrderTotalProjection::getTotalAmount
            ));
    }

    public OrdersEntity getOrder(Long id) {
        return repository.findWithDetailById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }

    public AppUserEntity getUserReference(Long userId) {
        return appUserRepo.getReferenceById(userId);
    }

    public OrdersEntity createOrder(OrdersEntity entity) {
        return repository.save(entity);
    }

    /**
     * Apply a header status transition, validating it first and recording related dates.
     */
    public void updateOrderStatus(OrdersEntity entity, OrderStatusEntity newStatus) {
        validateStatusTransition(entity.getOrderStatus(), newStatus);
        if (CODE_PAID.equals(newStatus.getCode()) && entity.getPaidDate() == null) {
            entity.setPaidDate(LocalDate.now());
        }
        if (CODE_SHIPPED.equals(newStatus.getCode()) && entity.getShippedDate() == null) {
            entity.setShippedDate(LocalDate.now());
        }
        entity.setOrderStatus(newStatus);
        repository.save(entity);
    }

    /**
     * Header status transition rules:
     * - same status is rejected (no-op change)
     * - CANCELLED is allowed from any non-terminal status (not from DELIVERED/CANCELLED)
     * - otherwise only forward progression by one or more ranks is allowed (no backward moves)
     */
    public void validateStatusTransition(OrderStatusEntity current, OrderStatusEntity next) {
        if (current == null) {
            return;
        }
        var currentCode = current.getCode();
        var nextCode = next.getCode();
        if (currentCode.equals(nextCode)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
        }
        // terminal states cannot transition further
        if (CODE_CANCELLED.equals(currentCode) || CODE_DELIVERED.equals(currentCode)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
        }
        if (CODE_CANCELLED.equals(nextCode)) {
            return;
        }
        var currentRank = STATUS_RANK.get(currentCode);
        var nextRank = STATUS_RANK.get(nextCode);
        if (currentRank == null || nextRank == null || nextRank <= currentRank) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
        }
    }
}
