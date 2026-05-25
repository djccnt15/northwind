package com.djccnt15.northwind.global.config.security;

import com.djccnt15.northwind.global.code.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.djccnt15.northwind.constants.ApiTestConst.API_BODY_PATH;
import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static com.djccnt15.northwind.constants.TestConst.SYSTEM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthConfigTest {
    
    @Autowired private MockMvc mockMvc;
    
    @Test
    void loginSuccess() throws Exception {
        var mvcResult = mockMvc.perform(post("/api/public/v1/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", SYSTEM)
                .param("password", SYSTEM))
            .andExpect(status().isOk())
            .andReturn();
        
        // forward된 URL로 재요청 (세션 쿠키 유지)
        var session = (MockHttpSession) mvcResult.getRequest().getSession();
        mockMvc.perform(post(mvcResult.getResponse().getForwardedUrl())
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH)
                .value(StatusCode.OK.getStatusCode()))
            .andExpect(jsonPath(API_BODY_PATH + ".username")
                .value(SYSTEM))
        ;
    }
    
    @Test
    void loginFail() throws Exception {
        var mvcResult = mockMvc.perform(post("/api/public/v1/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", SYSTEM)
                .param("password", "wrong"))
            .andExpect(status().isOk())  // forward 전이므로 아직 200
            .andReturn();
        
        // forward된 URL로 재요청 (request attribute 포함을 위해 session 유지)
        var session = (MockHttpSession) mvcResult.getRequest().getSession();
        mockMvc.perform(post(mvcResult.getResponse().getForwardedUrl())
                .session(session)
                .requestAttr("exception", mvcResult.getRequest().getAttribute("exception")))  // exception attribute 전달
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath(API_CODE_PATH)
                .value(StatusCode.UNAUTHORIZED.getStatusCode()))
        ;
    }
}
