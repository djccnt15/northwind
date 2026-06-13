package com.djccnt15.northwind.domain.stocktake.controller;

import com.djccnt15.northwind.domain.stocktake.business.StockTakeBusiness;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeItemReq;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeRowRes;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeSaveReq;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(StockTakeApiController.class)
class StockTakeApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private StockTakeBusiness business;
    @MockitoBean private MessageUtil messageUtil;

    private StockTakeSaveReq sampleSaveReq() {
        return new StockTakeSaveReq(
            LocalDate.now(),
            List.of(new StockTakeItemReq(1L, 35L))
        );
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/stock-takes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "STOCK")
    void getStockTakeRows_stockAuthority_ok() throws Exception {
        when(business.getStockTakeRows(any(), anyInt(), anyInt()))
            .thenReturn(new PageImpl<>(List.of(
                StockTakeRowRes.builder()
                    .productId(1L).productCode("P001").productName("Chai")
                    .expectedQuantity(0L).quantityOnHand(null).build()
            )));
        mockMvc.perform(get("/api/v1/stock-takes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getStockTakeRows_adminAuthority_ok() throws Exception {
        when(business.getStockTakeRows(any(), anyInt(), anyInt()))
            .thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/api/v1/stock-takes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "STOCK")
    void saveStockTakes_ok() throws Exception {
        when(business.saveStockTakes(any()))
            .thenReturn(List.of(StockTakeRowRes.builder().productId(1L).build()));
        mockMvc.perform(post("/api/v1/stock-takes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleSaveReq())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "STOCK")
    void saveStockTakes_validationFail() throws Exception {
        var request = sampleSaveReq();
        request.setStockTakeDate(null);
        request.setItems(List.of());
        mockMvc.perform(post("/api/v1/stock-takes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }

    @Test
    @WithMockUser(authorities = "STOCK")
    void saveStockTakes_negativeQuantityValidationFail() throws Exception {
        var request = new StockTakeSaveReq(LocalDate.now(), List.of(new StockTakeItemReq(1L, -1L)));
        mockMvc.perform(post("/api/v1/stock-takes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }
}
