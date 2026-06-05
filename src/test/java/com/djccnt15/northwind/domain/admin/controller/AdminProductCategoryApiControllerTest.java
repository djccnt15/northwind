package com.djccnt15.northwind.domain.admin.controller;

import com.djccnt15.northwind.domain.admin.business.AdminProductCategoryBusiness;
import com.djccnt15.northwind.domain.product.model.ProductCategoryRes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(AdminProductCategoryApiController.class)
@Import(AdminProductCategoryApiControllerTest.MethodSecurityConfig.class)
class AdminProductCategoryApiControllerTest {

    @EnableMethodSecurity
    static class MethodSecurityConfig {
    }

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AdminProductCategoryBusiness business;

    @Test
    void unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/admin/categories/all"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void forbidden() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/admin/categories/all"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllCategories() throws Exception {
        // given
        when(business.getCategories()).thenReturn(List.of(
            ProductCategoryRes.builder().id(1L).name("Beverages").code("BEV").build()
        ));

        // when & then
        mockMvc.perform(get("/api/v1/admin/categories/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }
}
