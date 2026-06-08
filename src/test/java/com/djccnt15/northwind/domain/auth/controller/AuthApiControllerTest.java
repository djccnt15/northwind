package com.djccnt15.northwind.domain.auth.controller;

import com.djccnt15.northwind.domain.user.converter.UserConverter;
import com.djccnt15.northwind.global.code.StatusCode;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(AuthApiController.class)
class AuthApiControllerTest {
    
    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserConverter converter;
    @MockitoBean private MessageUtil messageUtil;
    
    @Test
    @WithMockUser
    void checkSession() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/auth/check-session"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH)
                .value(StatusCode.VALIDATED.getStatusCode()))
            ;
    }
    
    @Test
    void checkSessionFail() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/auth/check-session"))
            .andExpect(status().isUnauthorized())
            ;
    }
}
