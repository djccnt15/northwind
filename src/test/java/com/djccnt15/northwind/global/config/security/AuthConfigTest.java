package com.djccnt15.northwind.global.config.security;

import com.djccnt15.northwind.global.code.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.web.servlet.MockMvc;

import static com.djccnt15.northwind.constants.ApiTestConst.API_BODY_PATH;
import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static com.djccnt15.northwind.constants.TestConst.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthConfigTest {
    
    @Autowired private MockMvc mockMvc;
    
    @Test
    void loginSuccess() throws Exception {
        // given
        var tokenResult = mockMvc.perform(get("/api/public/v1/auth/csrf-token"))
            .andExpect(status().isOk())
            .andReturn();
        
        var tokenRequest = tokenResult.getRequest();
        var session = (MockHttpSession) tokenRequest.getSession();
        var tmpCsrfToken = (CsrfToken) tokenRequest.getAttribute(CSRF_ATTRIBUTE_NAME);
        var xsrfCookie = tokenResult.getResponse().getCookie(XSRF_COOKIE_NAME);
        
        // when & then
        var loginResult = mockMvc.perform(post("/api/public/v1/login")
                .session(session)
                .cookie(xsrfCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(USERNAME, SYSTEM)
                .param(PASSWORD, SYSTEM)
                .header(XSRF_HEADER_NAME, tmpCsrfToken.getToken()))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/api/v1/auth/login/success"))
            .andReturn();
        
        // then
        assertNotNull(loginResult.getRequest().getSession(false));
        assertNotNull(loginResult.getRequest().getSession(false)
            .getAttribute("SPRING_SECURITY_CONTEXT"));
    }
    
    @Test
    void loginSuccessResult() throws Exception {
        // given
        var tokenResult = mockMvc.perform(get("/api/public/v1/auth/csrf-token"))
            .andExpect(status().isOk())
            .andReturn();
        
        var tokenRequest = tokenResult.getRequest();
        var session = (MockHttpSession) tokenRequest.getSession();
        var tmpCsrfToken = (CsrfToken) tokenRequest.getAttribute(CSRF_ATTRIBUTE_NAME);
        var xsrfCookie = tokenResult.getResponse().getCookie(XSRF_COOKIE_NAME);
        
        // when & then
        var loginResult = mockMvc.perform(post("/api/public/v1/login")
                .session(session)
                .cookie(xsrfCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(USERNAME, SYSTEM)
                .param(PASSWORD, SYSTEM)
                .header(XSRF_HEADER_NAME, tmpCsrfToken.getToken()))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/api/v1/auth/login/success"))
            .andReturn();
        
        var loginRequest = loginResult.getRequest();
        var csrfToken = (CsrfToken) loginRequest.getAttribute(CSRF_ATTRIBUTE_NAME);
        var loginCookie = loginResult.getResponse().getCookie(XSRF_COOKIE_NAME);
        loginCookie.setValue(csrfToken.getToken());
        
        // then - forward된 URL로 재요청 (request attribute 포함을 위해 session 유지)
        mockMvc.perform(post(loginResult.getResponse().getForwardedUrl())
                .session(session)
                .cookie(loginCookie)
                .header(XSRF_HEADER_NAME, csrfToken.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(StatusCode.OK.getStatusCode()))
            .andExpect(jsonPath(API_BODY_PATH + ".username").value(SYSTEM))
        ;
    }
    
    @Test
    void loginFail() throws Exception {
        // given
        var tokenResult = mockMvc.perform(get("/api/public/v1/auth/csrf-token"))
            .andExpect(status().isOk())
            .andReturn();
        
        var tokenRequest = tokenResult.getRequest();
        var session = (MockHttpSession) tokenRequest.getSession();
        var tmpCsrfToken = (CsrfToken) tokenRequest.getAttribute(CSRF_ATTRIBUTE_NAME);
        var xsrfCookie = tokenResult.getResponse().getCookie(XSRF_COOKIE_NAME);
        
        // when & then
        var loginResult = mockMvc.perform(post("/api/public/v1/login")
                .session(session)
                .cookie(xsrfCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(USERNAME, SYSTEM)
                .param(PASSWORD, "wrong")
                .header(XSRF_HEADER_NAME, tmpCsrfToken.getToken()))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/api/public/v1/auth/login/fail"))
            .andReturn();
        
        // then
        assertNotNull(loginResult.getRequest().getSession(false));
    }
    
    @Test
    void loginFailResult() throws Exception {
        // given
        var tokenResult = mockMvc.perform(get("/api/public/v1/auth/csrf-token"))
            .andExpect(status().isOk())
            .andReturn();
        
        var tokenRequest = tokenResult.getRequest();
        var session = (MockHttpSession) tokenRequest.getSession();
        var tmpCsrfToken = (CsrfToken) tokenRequest.getAttribute(CSRF_ATTRIBUTE_NAME);
        var xsrfCookie = tokenResult.getResponse().getCookie(XSRF_COOKIE_NAME);
        
        // when & then
        var loginResult = mockMvc.perform(post("/api/public/v1/login")
                .session(session)
                .cookie(xsrfCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(USERNAME, SYSTEM)
                .param(PASSWORD, "wrong")
                .header(XSRF_HEADER_NAME, tmpCsrfToken.getToken()))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/api/public/v1/auth/login/fail"))
            .andReturn();
        
        // forward된 URL로 재요청 (request attribute 포함을 위해 session 유지)
        mockMvc.perform(post(loginResult.getResponse().getForwardedUrl())
                .session(session)
                .cookie(xsrfCookie)
                .header(XSRF_HEADER_NAME, tmpCsrfToken.getToken())
                .requestAttr("exception", loginResult.getRequest().getAttribute("exception")))  // exception attribute 전달
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath(API_CODE_PATH)
                .value(StatusCode.UNAUTHORIZED.getStatusCode()))
        ;
    }
}
