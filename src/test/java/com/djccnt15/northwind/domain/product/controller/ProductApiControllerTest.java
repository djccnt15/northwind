package com.djccnt15.northwind.domain.product.controller;

import com.djccnt15.northwind.domain.product.business.ProductBusiness;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.domain.product.model.ProductCreateReq;
import com.djccnt15.northwind.domain.product.model.ProductRes;
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

import java.math.BigDecimal;
import java.util.List;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(ProductApiController.class)
class ProductApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ProductBusiness business;
    @MockitoBean private MessageUtil messageUtil;

    private ProductCreateReq sampleRequest() {
        return new ProductCreateReq(
            "P001",
            "Chai",
            "Sample description",
            new BigDecimal("10.00"),
            new BigDecimal("18.00"),
            10,
            40,
            10,
            5,
            false,
            1L
        );
    }

    @Test
    void unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getProducts() throws Exception {
        // given
        when(business.getProducts(anyInt(), anyInt(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(
                ProductRes.builder().id(1L).code("P001").name("Chai").build()
            )));

        // when & then
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser
    void getProduct() throws Exception {
        // given
        when(business.getProduct(any()))
            .thenReturn(ProductRes.builder().id(1L).code("P001").name("Chai").build());

        // when & then
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    void getAllCategoriesUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/categories/all"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllCategories() throws Exception {
        // given
        when(business.getCategories())
            .thenReturn(List.of(
                ProductCategoryRes.builder().id(1L).code("C001").name("Beverages").build()
            ));

        // when & then
        mockMvc.perform(get("/api/v1/categories/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    void createProductUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createProduct() throws Exception {
        // given
        when(business.createProduct(any()))
            .thenReturn(ProductRes.builder().id(1L).code("P001").name("Chai").build());

        // when & then
        mockMvc.perform(post("/api/v1/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(201));
    }

    @Test
    @WithMockUser
    void createProductValidationFail() throws Exception {
        // given
        var request = sampleRequest();
        request.setCode("");
        request.setName("");

        // when & then
        mockMvc.perform(post("/api/v1/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }

    @Test
    @WithMockUser
    void updateProduct() throws Exception {
        // given
        when(business.updateProduct(any(), any()))
            .thenReturn(ProductRes.builder().id(1L).code("P001").name("Chai").build());

        // when & then
        mockMvc.perform(put("/api/v1/products/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser
    void discontinueProduct() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/products/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
        verify(business).discontinueProduct(1L);
    }
}
