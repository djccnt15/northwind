package com.djccnt15.northwind.domain.product.controller;

import com.djccnt15.northwind.domain.product.business.ProductBusiness;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import com.djccnt15.northwind.domain.product.model.ProductRes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(ProductApiController.class)
class ProductApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProductBusiness business;

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
}
