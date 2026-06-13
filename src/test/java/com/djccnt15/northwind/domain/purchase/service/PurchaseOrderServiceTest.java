package com.djccnt15.northwind.domain.purchase.service;

import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusUpdateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseOrderServiceTest {

    @Autowired private PurchaseOrderService purchaseOrderService;
    @Autowired private PurchaseOrderStatusService purchaseOrderStatusService;

    private static final String KW_ALL = "%%";

    @Test
    void getPurchaseOrders_returnsSeededPurchaseOrder() {
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "submittedDate"));
        var result = purchaseOrderService.getPurchaseOrders(KW_ALL, null, null, null, pageable);
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    void getPurchaseOrders_filterByStatus() {
        var draft = purchaseOrderStatusService.getFirstPurchaseOrderStatus();
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "submittedDate"));
        var result = purchaseOrderService.getPurchaseOrders(KW_ALL, draft.getId(), null, null, pageable);
        assertTrue(result.getContent().stream()
            .allMatch(po -> po.getStatus().getId().equals(draft.getId())));
    }

    @Test
    void getPurchaseOrders_filterByDateRange() {
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "submittedDate"));
        var future = purchaseOrderService.getPurchaseOrders(
            KW_ALL, null, LocalDate.now().plusYears(1), null, pageable);
        assertTrue(future.getContent().isEmpty());
    }

    @Test
    void getPurchaseOrder_notFound() {
        assertThrows(ApiException.class, () -> purchaseOrderService.getPurchaseOrder(-1L));
    }

    @Test
    void getPurchaseOrder_withDetails() {
        var po = purchaseOrderService.getPurchaseOrder(1L);
        assertNotNull(po);
        assertFalse(po.getPurchaseOrderDetails().isEmpty());
    }

    @Test
    void getTotalAmounts_aggregatesSubtotalPlusShipping() {
        // seeded po 1: qty 5 * unitPrice 10 = 50 + shippingFee 100 = 150
        var totals = purchaseOrderService.getTotalAmounts(java.util.List.of(1L));
        assertEquals(0, totals.get(1L).compareTo(new java.math.BigDecimal("150")));
    }

    @Test
    @Transactional
    void updatePurchaseOrderStatus_approvedTransition_setsApprovedDate() {
        var po = purchaseOrderService.getPurchaseOrder(1L);
        // DRAFT(1) -> PENDING_APPROVAL(2) -> APPROVED(3)
        var pending = purchaseOrderStatusService.getPurchaseOrderStatus(2L);
        purchaseOrderService.updatePurchaseOrderStatus(po, pending, null, new PurchaseOrderStatusUpdateReq());
        var approved = purchaseOrderStatusService.getPurchaseOrderStatus(3L);
        purchaseOrderService.updatePurchaseOrderStatus(po, approved, null, new PurchaseOrderStatusUpdateReq());
        assertEquals("APPROVED", po.getStatus().getCode());
        assertEquals(LocalDate.now(), po.getApprovedDate());
    }

    @Test
    @Transactional
    void updatePurchaseOrderStatus_paidTransition_recordsPaymentFields() {
        var po = purchaseOrderService.getPurchaseOrder(1L);
        // walk DRAFT -> PENDING_APPROVAL -> APPROVED -> RECEIVED -> PAID
        for (long id = 2; id <= 5; id++) {
            var status = purchaseOrderStatusService.getPurchaseOrderStatus(id);
            var req = new PurchaseOrderStatusUpdateReq(id, LocalDate.now(), 999, "BANK_TRANSFER");
            purchaseOrderService.updatePurchaseOrderStatus(po, status, null, req);
        }
        assertEquals("PAID", po.getStatus().getCode());
        assertEquals(LocalDate.now(), po.getPaymentDate());
        assertEquals(999, po.getPaymentAmount());
        assertEquals("BANK_TRANSFER", po.getPaymentMethod());
    }

    @Test
    void validateStatusTransition_blocksBackwardAndSame() {
        var draft = purchaseOrderStatusService.getPurchaseOrderStatus(1L);
        var approved = purchaseOrderStatusService.getPurchaseOrderStatus(3L);
        // forward ok
        assertDoesNotThrow(() -> purchaseOrderService.validateStatusTransition(draft, approved));
        // backward blocked
        assertThrows(ApiException.class,
            () -> purchaseOrderService.validateStatusTransition(approved, draft));
        // same status blocked
        assertThrows(ApiException.class,
            () -> purchaseOrderService.validateStatusTransition(draft, draft));
    }

    @Test
    void validateStatusTransition_rejectedReachableOnlyFromDraftOrPending_terminal() {
        var draft = purchaseOrderStatusService.getPurchaseOrderStatus(1L);
        var pending = purchaseOrderStatusService.getPurchaseOrderStatus(2L);
        var approved = purchaseOrderStatusService.getPurchaseOrderStatus(3L);
        var rejected = purchaseOrderStatusService.getPurchaseOrderStatus(6L);
        // reject from DRAFT / PENDING_APPROVAL ok
        assertDoesNotThrow(() -> purchaseOrderService.validateStatusTransition(draft, rejected));
        assertDoesNotThrow(() -> purchaseOrderService.validateStatusTransition(pending, rejected));
        // reject from APPROVED not allowed
        assertThrows(ApiException.class,
            () -> purchaseOrderService.validateStatusTransition(approved, rejected));
        // REJECTED is terminal
        assertThrows(ApiException.class,
            () -> purchaseOrderService.validateStatusTransition(rejected, draft));
    }

    @Test
    void validateStatusTransition_paidIsTerminal() {
        var paid = purchaseOrderStatusService.getPurchaseOrderStatus(5L);
        var draft = purchaseOrderStatusService.getPurchaseOrderStatus(1L);
        assertThrows(ApiException.class,
            () -> purchaseOrderService.validateStatusTransition(paid, draft));
    }
}
