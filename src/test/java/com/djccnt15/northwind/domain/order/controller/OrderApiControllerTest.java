package com.djccnt15.northwind.domain.order.controller;

import com.djccnt15.northwind.domain.order.business.OrderBusiness;
import com.djccnt15.northwind.domain.order.model.OrderCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderListRes;
import com.djccnt15.northwind.domain.order.model.OrderRes;
import com.djccnt15.northwind.domain.order.model.OrderStatusRes;
import com.djccnt15.northwind.global.message.MessageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(OrderApiController.class)
class OrderApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private OrderBusiness business;
    @MockitoBean private MessageUtil messageUtil;

    private OrderCreateReq sampleCreateReq() {
        return new OrderCreateReq(
            1L, 2L, LocalDate.now().plusDays(7), 1L, "CARD", 50, "notes",
            List.of(new OrderDetailCreateReq(1L, 2, 0))
        );
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void getOrders_orderAuthority_ok() throws Exception {
        when(business.getOrders(anyInt(), anyInt(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(
                OrderListRes.builder().id(1L).customerName("Acme Corp").build()
            )));
        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getOrderStatuses_adminAuthority_ok() throws Exception {
        when(business.getOrderStatuses())
            .thenReturn(List.of(OrderStatusRes.builder().id(1L).code("PENDING").name("접수").build()));
        mockMvc.perform(get("/api/v1/order-statuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void getCompanyOptions_lookupUnderOrderAuthority_ok() throws Exception {
        when(business.getCompanyOptions(any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/orders/companies").param("type", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void getOrder_ok() throws Exception {
        when(business.getOrder(any())).thenReturn(OrderRes.builder().id(1L).build());
        mockMvc.perform(get("/api/v1/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void createOrder_returns201() throws Exception {
        when(business.createOrder(any(), any())).thenReturn(OrderRes.builder().id(1L).build());
        mockMvc.perform(post("/api/v1/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCreateReq())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(201));
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void createOrder_validationFail() throws Exception {
        var request = sampleCreateReq();
        request.setCustomerId(null);
        request.setOrderDetails(List.of());
        mockMvc.perform(post("/api/v1/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void updateOrderStatus_ok() throws Exception {
        when(business.updateOrderStatus(any(), any())).thenReturn(OrderRes.builder().id(1L).build());
        mockMvc.perform(patch("/api/v1/orders/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statusId\": 2}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ORDER")
    void updateOrderDetailStatus_ok() throws Exception {
        when(business.updateOrderDetailStatus(any(), any(), any()))
            .thenReturn(OrderRes.builder().id(1L).build());
        mockMvc.perform(patch("/api/v1/orders/1/details/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statusId\": 3}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }
}
