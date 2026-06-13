package com.djccnt15.northwind.domain.purchase.controller;

import com.djccnt15.northwind.domain.purchase.business.PurchaseOrderBusiness;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderDetailCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderListRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusRes;
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
@WebMvcTest(PurchaseOrderApiController.class)
class PurchaseOrderApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private PurchaseOrderBusiness business;
    @MockitoBean private MessageUtil messageUtil;

    private PurchaseOrderCreateReq sampleCreateReq() {
        return new PurchaseOrderCreateReq(
            2L, 50, null, "note",
            List.of(new PurchaseOrderDetailCreateReq(1L, 3, null))
        );
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/purchase-orders"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "PURCHASE")
    void getPurchaseOrders_purchaseAuthority_ok() throws Exception {
        when(business.getPurchaseOrders(anyInt(), anyInt(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(
                PurchaseOrderListRes.builder().id(1L).vendorName("Fast Shipping Inc").build()
            )));
        mockMvc.perform(get("/api/v1/purchase-orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getPurchaseOrderStatuses_adminAuthority_ok() throws Exception {
        when(business.getPurchaseOrderStatuses())
            .thenReturn(List.of(PurchaseOrderStatusRes.builder().id(1L).code("DRAFT").name("작성중").build()));
        mockMvc.perform(get("/api/v1/purchase-order-statuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "PURCHASE")
    void getVendorOptions_lookupUnderPurchaseAuthority_ok() throws Exception {
        when(business.getVendorOptions(any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/purchase-orders/companies").param("type", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "PURCHASE")
    void getPurchaseOrder_ok() throws Exception {
        when(business.getPurchaseOrder(any())).thenReturn(PurchaseOrderRes.builder().id(1L).build());
        mockMvc.perform(get("/api/v1/purchase-orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "PURCHASE")
    void createPurchaseOrder_returns201() throws Exception {
        when(business.createPurchaseOrder(any(), any())).thenReturn(PurchaseOrderRes.builder().id(1L).build());
        mockMvc.perform(post("/api/v1/purchase-orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCreateReq())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(201));
    }

    @Test
    @WithMockUser(authorities = "PURCHASE")
    void createPurchaseOrder_validationFail() throws Exception {
        var request = sampleCreateReq();
        request.setVendorId(null);
        request.setPurchaseOrderDetails(List.of());
        mockMvc.perform(post("/api/v1/purchase-orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }

    @Test
    @WithMockUser(authorities = "PURCHASE")
    void updatePurchaseOrderStatus_ok() throws Exception {
        when(business.updatePurchaseOrderStatus(any(), any(), any()))
            .thenReturn(PurchaseOrderRes.builder().id(1L).build());
        mockMvc.perform(patch("/api/v1/purchase-orders/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statusId\": 2}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }
}
