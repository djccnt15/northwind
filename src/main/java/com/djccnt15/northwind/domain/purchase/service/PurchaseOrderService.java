package com.djccnt15.northwind.domain.purchase.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.db.projection.PurchaseOrderTotalProjection;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.PurchaseOrderRepo;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusUpdateReq;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderErrorConst.INVALID_STATUS_TRANSITION_ERR_MSG;
import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderErrorConst.NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    public static final String CODE_DRAFT = "DRAFT";
    public static final String CODE_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String CODE_APPROVED = "APPROVED";
    public static final String CODE_RECEIVED = "RECEIVED";
    public static final String CODE_PAID = "PAID";
    public static final String CODE_REJECTED = "REJECTED";

    // forward progression rank by status code; REJECTED handled separately as a terminal state
    private static final Map<String, Integer> STATUS_RANK = Map.of(
        CODE_DRAFT, 1,
        CODE_PENDING_APPROVAL, 2,
        CODE_APPROVED, 3,
        CODE_RECEIVED, 4,
        CODE_PAID, 5
    );

    // REJECTED is only reachable from these statuses
    private static final Set<String> REJECTABLE_FROM = Set.of(CODE_DRAFT, CODE_PENDING_APPROVAL);

    private final PurchaseOrderRepo repository;
    private final AppUserRepo appUserRepo;
    private final MessageUtil messageUtil;

    public Page<PurchaseOrderEntity> getPurchaseOrders(
        String kw, Long statusId, LocalDate dateFrom, LocalDate dateTo, Pageable pageable
    ) {
        return repository.findByFilter(kw, statusId, dateFrom, dateTo, pageable);
    }

    public Map<Long, BigDecimal> getTotalAmounts(List<Long> purchaseOrderIds) {
        if (purchaseOrderIds.isEmpty()) {
            return Map.of();
        }
        return repository.findTotalAmountByIdIn(purchaseOrderIds).stream()
            .collect(Collectors.toMap(
                PurchaseOrderTotalProjection::getPurchaseOrderId,
                PurchaseOrderTotalProjection::getTotalAmount
            ));
    }

    public PurchaseOrderEntity getPurchaseOrder(Long id) {
        return repository.findWithDetailById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }

    public AppUserEntity getUserReference(Long userId) {
        return appUserRepo.getReferenceById(userId);
    }

    public PurchaseOrderEntity createPurchaseOrder(PurchaseOrderEntity entity) {
        return repository.save(entity);
    }

    /**
     * Apply a header status transition, validating it first and recording related dates.
     * The approver employee is resolved by the business layer and passed in for APPROVED transitions.
     */
    public void updatePurchaseOrderStatus(
        PurchaseOrderEntity entity,
        PurchaseOrderStatusEntity newStatus,
        EmployeeEntity approver,
        PurchaseOrderStatusUpdateReq request
    ) {
        validateStatusTransition(entity.getStatus(), newStatus);
        var code = newStatus.getCode();
        if (CODE_APPROVED.equals(code)) {
            if (entity.getApprovedDate() == null) {
                entity.setApprovedDate(LocalDate.now());
            }
            if (approver != null) {
                entity.setApprovedBy(approver);
            }
        }
        if (CODE_RECEIVED.equals(code) && entity.getReceivedDate() == null) {
            entity.setReceivedDate(LocalDate.now());
        }
        if (CODE_PAID.equals(code)) {
            entity.setPaymentDate(request.getPaymentDate() != null
                ? request.getPaymentDate()
                : LocalDate.now());
            if (request.getPaymentAmount() != null) {
                entity.setPaymentAmount(request.getPaymentAmount());
            }
            if (request.getPaymentMethod() != null) {
                entity.setPaymentMethod(request.getPaymentMethod());
            }
        }
        entity.setStatus(newStatus);
        repository.save(entity);
    }

    /**
     * Header status transition rules:
     * - same status is rejected (no-op change)
     * - REJECTED is a terminal state reachable only from DRAFT or PENDING_APPROVAL
     * - terminal states (PAID, REJECTED) cannot transition further
     * - otherwise only forward progression by one or more ranks is allowed (no backward moves)
     */
    public void validateStatusTransition(PurchaseOrderStatusEntity current, PurchaseOrderStatusEntity next) {
        if (current == null) {
            return;
        }
        var currentCode = current.getCode();
        var nextCode = next.getCode();
        if (currentCode.equals(nextCode)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
        }
        // terminal states cannot transition further
        if (CODE_REJECTED.equals(currentCode) || CODE_PAID.equals(currentCode)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
        }
        if (CODE_REJECTED.equals(nextCode)) {
            if (!REJECTABLE_FROM.contains(currentCode)) {
                throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
            }
            return;
        }
        var currentRank = STATUS_RANK.get(currentCode);
        var nextRank = STATUS_RANK.get(nextCode);
        if (currentRank == null || nextRank == null || nextRank <= currentRank) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(INVALID_STATUS_TRANSITION_ERR_MSG));
        }
    }
}
