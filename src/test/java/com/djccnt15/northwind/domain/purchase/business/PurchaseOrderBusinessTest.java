package com.djccnt15.northwind.domain.purchase.business;

import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderDetailCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusUpdateReq;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseOrderBusinessTest {

    @Autowired private PurchaseOrderBusiness purchaseOrderBusiness;

    private UserSession purchaserSession() {
        // app_user 3 (purchaser) is linked to a seeded employee record
        return UserSession.builder().id(3L).username("purchaser").build();
    }

    private PurchaseOrderCreateReq sampleRequest() {
        return new PurchaseOrderCreateReq(
            2L,                 // vendorId (Fast Shipping Inc / Supplier)
            50,                 // shippingFee
            null,               // taxAmount
            "test purchase order",
            List.of(new PurchaseOrderDetailCreateReq(1L, 3, null))  // qty 3, default unit price
        );
    }

    @Test
    void getPurchaseOrders_returnsPagedListWithTotal() {
        var page = purchaseOrderBusiness.getPurchaseOrders(0, 20, null, null, null, "");
        assertFalse(page.getContent().isEmpty());
        assertNotNull(page.getContent().get(0).getTotalAmount());
    }

    @Test
    void getPurchaseOrder_returnsDetail() {
        var po = purchaseOrderBusiness.getPurchaseOrder(1L);
        assertNotNull(po);
        assertEquals(1L, po.getId());
        assertFalse(po.getPurchaseOrderDetails().isEmpty());
        assertNotNull(po.getSubmittedBy());
    }

    @Test
    @Transactional
    void createPurchaseOrder_defaultsUnitPriceToStandardCost() {
        var created = purchaseOrderBusiness.createPurchaseOrder(sampleRequest(), purchaserSession());

        assertNotNull(created.getId());
        assertEquals(LocalDate.now(), created.getSubmittedDate());
        assertEquals("DRAFT", created.getStatus().getCode());
        assertEquals(1, created.getPurchaseOrderDetails().size());
        assertNotNull(created.getSubmittedBy());

        var detail = created.getPurchaseOrderDetails().get(0);
        // unitPrice defaulted to product standardUnitCost (10.00)
        assertEquals(0, detail.getUnitPrice().compareTo(new BigDecimal("10.00")));
        // subtotal = 10 * 3 = 30.00
        assertEquals(0, detail.getSubtotal().compareTo(new BigDecimal("30.00")));
        // total = 30.00 + shippingFee 50 = 80.00
        assertEquals(0, created.getTotalAmount().compareTo(new BigDecimal("80.00")));
    }

    @Test
    @Transactional
    void updatePurchaseOrderStatus_recordsApproverOnApprove() {
        // DRAFT(1) -> PENDING_APPROVAL(2) -> APPROVED(3)
        purchaseOrderBusiness.updatePurchaseOrderStatus(1L, new PurchaseOrderStatusUpdateReq(
            2L, null, null, null), purchaserSession());
        var approved = purchaseOrderBusiness.updatePurchaseOrderStatus(1L, new PurchaseOrderStatusUpdateReq(
            3L, null, null, null), purchaserSession());
        assertEquals("APPROVED", approved.getStatus().getCode());
        assertEquals(LocalDate.now(), approved.getApprovedDate());
        assertNotNull(approved.getApprovedBy());
    }

    @Test
    @Transactional
    void updatePurchaseOrderStatus_rejectsInvalidTransition() {
        // DRAFT(1) -> APPROVED(3) is forward and allowed; then APPROVED -> DRAFT must fail
        purchaseOrderBusiness.updatePurchaseOrderStatus(1L, new PurchaseOrderStatusUpdateReq(
            3L, null, null, null), purchaserSession());
        assertThrows(ApiException.class,
            () -> purchaseOrderBusiness.updatePurchaseOrderStatus(1L, new PurchaseOrderStatusUpdateReq(
                1L, null, null, null), purchaserSession()));
    }

    @Test
    void updatePurchaseOrderStatus_notFound() {
        assertThrows(ApiException.class,
            () -> purchaseOrderBusiness.updatePurchaseOrderStatus(999L, new PurchaseOrderStatusUpdateReq(
                2L, null, null, null), purchaserSession()));
    }

    @Test
    void lookups_workUnderPurchaseBusiness() {
        assertFalse(purchaseOrderBusiness.getPurchaseOrderStatuses().isEmpty());
        assertFalse(purchaseOrderBusiness.getCompanyTypes().isEmpty());
        assertFalse(purchaseOrderBusiness.getVendorOptions(null, "").isEmpty());
        assertFalse(purchaseOrderBusiness.getProductOptions("").isEmpty());
    }
}
