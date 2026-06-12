package com.djccnt15.northwind.domain.order.business;

import com.djccnt15.northwind.domain.order.model.OrderCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusUpdateReq;
import com.djccnt15.northwind.domain.order.model.OrderStatusUpdateReq;
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
class OrderBusinessTest {

    @Autowired private OrderBusiness orderBusiness;

    private UserSession systemSession() {
        return UserSession.builder().id(1L).username("system").build();
    }

    private OrderCreateReq sampleRequest() {
        return new OrderCreateReq(
            1L,              // customerId
            2L,              // shipperId
            LocalDate.now().plusDays(7),
            1L,              // taxStatusId
            "CARD",
            50,              // shippingFee
            "test order",
            List.of(new OrderDetailCreateReq(1L, 3, 10))  // qty 3, discount 10
        );
    }

    @Test
    void getOrders_returnsPagedListWithTotal() {
        var page = orderBusiness.getOrders(0, 20, null, null, null, "");
        assertFalse(page.getContent().isEmpty());
        assertNotNull(page.getContent().get(0).getTotalAmount());
    }

    @Test
    void getOrder_returnsDetail() {
        var order = orderBusiness.getOrder(1L);
        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertFalse(order.getOrderDetails().isEmpty());
    }

    @Test
    @Transactional
    void createOrder_persistsWithSnapshotPriceAndTotal() {
        var created = orderBusiness.createOrder(sampleRequest(), systemSession());

        assertNotNull(created.getId());
        assertEquals(LocalDate.now(), created.getOrderDate());
        assertEquals("PENDING", created.getStatus().getCode());
        assertEquals(1, created.getOrderDetails().size());

        var detail = created.getOrderDetails().get(0);
        // unitPrice snapshot copied from product (18.00)
        assertEquals(0, detail.getUnitPrice().compareTo(new BigDecimal("18.00")));
        // subtotal = 18 * 3 * (1 - 10/100) = 48.60
        assertEquals(0, detail.getSubtotal().compareTo(new BigDecimal("48.60")));
        // total = 48.60 + shippingFee 50 = 98.60
        assertEquals(0, created.getTotalAmount().compareTo(new BigDecimal("98.60")));
    }

    @Test
    @Transactional
    void updateOrderStatus_appliesForwardTransition() {
        var updated = orderBusiness.updateOrderStatus(1L, new OrderStatusUpdateReq(2L)); // PAID
        assertEquals("PAID", updated.getStatus().getCode());
        assertEquals(LocalDate.now(), updated.getPaidDate());
    }

    @Test
    @Transactional
    void updateOrderStatus_rejectsInvalidTransition() {
        // 1 -> DELIVERED is forward and allowed; then DELIVERED -> PENDING must fail
        orderBusiness.updateOrderStatus(1L, new OrderStatusUpdateReq(4L));
        assertThrows(ApiException.class,
            () -> orderBusiness.updateOrderStatus(1L, new OrderStatusUpdateReq(1L)));
    }

    @Test
    @Transactional
    void updateOrderDetailStatus_changesIndependently() {
        var updated = orderBusiness.updateOrderDetailStatus(1L, 1L, new OrderDetailStatusUpdateReq(3L)); // 취소
        var detail = updated.getOrderDetails().get(0);
        assertEquals(3L, detail.getStatus().getId());
        // header status unaffected
        assertEquals("PENDING", updated.getStatus().getCode());
    }

    @Test
    void updateOrderDetailStatus_wrongOrderId_notFound() {
        assertThrows(ApiException.class,
            () -> orderBusiness.updateOrderDetailStatus(999L, 1L, new OrderDetailStatusUpdateReq(3L)));
    }

    @Test
    void lookups_workUnderOrderBusiness() {
        assertFalse(orderBusiness.getOrderStatuses().isEmpty());
        assertFalse(orderBusiness.getOrderDetailStatuses().isEmpty());
        assertFalse(orderBusiness.getCompanyTypes().isEmpty());
        assertFalse(orderBusiness.getTaxStatuses().isEmpty());
        assertFalse(orderBusiness.getCompanyOptions(null, "").isEmpty());
        assertFalse(orderBusiness.getProductOptions("").isEmpty());
    }
}
