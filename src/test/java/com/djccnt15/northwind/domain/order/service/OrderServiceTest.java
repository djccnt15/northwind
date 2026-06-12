package com.djccnt15.northwind.domain.order.service;

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
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private OrderStatusService orderStatusService;

    private static final String KW_ALL = "%%";

    @Test
    void getOrders_returnsSeededOrder() {
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "orderDate"));
        var result = orderService.getOrders(KW_ALL, null, null, null, pageable);
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    void getOrders_filterByStatus() {
        var pending = orderStatusService.getFirstOrderStatus();
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "orderDate"));
        var result = orderService.getOrders(KW_ALL, pending.getId(), null, null, pageable);
        assertTrue(result.getContent().stream()
            .allMatch(o -> o.getOrderStatus().getId().equals(pending.getId())));
    }

    @Test
    void getOrders_filterByDateRange() {
        var pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "orderDate"));
        var future = orderService.getOrders(KW_ALL, null, LocalDate.now().plusYears(1), null, pageable);
        assertTrue(future.getContent().isEmpty());
    }

    @Test
    void getOrder_notFound() {
        assertThrows(ApiException.class, () -> orderService.getOrder(-1L));
    }

    @Test
    void getOrder_withDetails() {
        var order = orderService.getOrder(1L);
        assertNotNull(order);
        assertFalse(order.getOrderDetails().isEmpty());
    }

    @Test
    void getTotalAmounts_aggregatesSubtotalPlusShipping() {
        // seeded order 1: qty 2 * unitPrice 18 * (1 - 0/100) = 36 + shippingFee 100 = 136
        var totals = orderService.getTotalAmounts(java.util.List.of(1L));
        assertEquals(0, totals.get(1L).compareTo(new java.math.BigDecimal("136")));
    }

    @Test
    @Transactional
    void updateOrderStatus_forwardTransition_setsPaidDate() {
        var order = orderService.getOrder(1L);
        var paid = orderStatusService.getOrderStatus(2L); // PAID
        orderService.updateOrderStatus(order, paid);
        assertEquals("PAID", order.getOrderStatus().getCode());
        assertEquals(LocalDate.now(), order.getPaidDate());
    }

    @Test
    void validateStatusTransition_blocksBackward() {
        var pending = orderStatusService.getOrderStatus(1L);
        var shipped = orderStatusService.getOrderStatus(3L);
        // forward ok
        assertDoesNotThrow(() -> orderService.validateStatusTransition(pending, shipped));
        // backward blocked
        assertThrows(ApiException.class,
            () -> orderService.validateStatusTransition(shipped, pending));
        // same status blocked
        assertThrows(ApiException.class,
            () -> orderService.validateStatusTransition(pending, pending));
    }

    @Test
    void validateStatusTransition_allowsCancel_blocksFromTerminal() {
        var pending = orderStatusService.getOrderStatus(1L);
        var delivered = orderStatusService.getOrderStatus(4L);
        var cancelled = orderStatusService.getOrderStatus(5L);
        // cancel from non-terminal ok
        assertDoesNotThrow(() -> orderService.validateStatusTransition(pending, cancelled));
        // cannot transition out of terminal states
        assertThrows(ApiException.class,
            () -> orderService.validateStatusTransition(delivered, cancelled));
        assertThrows(ApiException.class,
            () -> orderService.validateStatusTransition(cancelled, pending));
    }
}
