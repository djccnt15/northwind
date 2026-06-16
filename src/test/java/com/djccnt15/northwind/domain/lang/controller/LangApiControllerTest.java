package com.djccnt15.northwind.domain.lang.controller;

import com.djccnt15.northwind.db.entity.SupportedLangEntity;
import com.djccnt15.northwind.domain.lang.converter.LangConverter;
import com.djccnt15.northwind.domain.lang.model.LangRes;
import com.djccnt15.northwind.domain.lang.service.LangService;
import com.djccnt15.northwind.global.code.StatusCode;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(LangApiController.class)
class LangApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private LangService langService;
    @MockitoBean private LangConverter langConverter;
    @MockitoBean private MessageUtil messageUtil;

    @Test
    @WithMockUser
    void getLangs() throws Exception {
        // given
        var entity = new SupportedLangEntity("en");
        given(langService.getLangs()).willReturn(List.of(entity));
        given(langConverter.toResponse(entity))
            .willReturn(LangRes.builder().id(1L).lang("en").build());

        // when & then
        mockMvc.perform(get("/api/v1/lang"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(StatusCode.OK.getStatusCode()))
            .andExpect(jsonPath("$.body[0].lang").value("en"));
    }

    @Test
    void getLangsUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/lang"))
            .andExpect(status().isUnauthorized());
    }
}
