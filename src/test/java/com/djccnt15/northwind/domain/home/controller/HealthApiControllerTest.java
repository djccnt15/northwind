package com.djccnt15.northwind.domain.home.controller;

import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.djccnt15.northwind.constants.ApiTestConst.API_BODY_PATH;
import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(HealthApiController.class)
class HealthApiControllerTest {
    
    @Autowired private MockMvc mockMvc;
    @MockitoBean private MessageUtil messageUtil;

    @Test
    @WithMockUser
    void health() throws Exception {
        // when & then
        mockMvc.perform(get("/api/public/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200))
            .andExpect(jsonPath(API_BODY_PATH).value(1));
    }
    
    @Test
    @WithMockUser
    void ping() throws Exception {
        // when & then
        mockMvc.perform(get("/api/public/v1/ping"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200))
            .andExpect(jsonPath(API_BODY_PATH).value("pong"));
    }
}
