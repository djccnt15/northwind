package com.djccnt15.northwind.domain.user.business;

import com.djccnt15.northwind.domain.lang.service.LangService;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UpdateLangReq;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserBusinessLangTest {

    @Autowired private UserBusiness userBusiness;
    @Autowired private LangService langService;

    private UserSession systemSession() {
        return UserSession.builder().id(1L).username("system").build();
    }

    private SignupReq signupRequest(String langCode) {
        return new SignupReq(
            "languser_" + System.nanoTime(),
            "password1",
            "lang_" + System.nanoTime() + "@test.com",
            true,
            null,
            "password1",
            null,
            null,
            langCode
        );
    }

    @Test
    @Transactional
    void createUser_assignsRequestedLang() {
        var response = userBusiness.createUser(signupRequest("ko"));

        assertEquals("ko", response.getPreferredLang());
    }

    @Test
    @Transactional
    void createUser_fallsBackToDefaultLangWhenNull() {
        var response = userBusiness.createUser(signupRequest(null));

        assertEquals(LangService.DEFAULT_LANG, response.getPreferredLang());
    }

    @Test
    @Transactional
    void createUser_fallsBackToDefaultLangWhenUnknown() {
        var response = userBusiness.createUser(signupRequest("fr"));

        assertEquals(LangService.DEFAULT_LANG, response.getPreferredLang());
    }

    @Test
    @Transactional
    void updateLang_updatesPreferredLangAndSession() {
        var session = systemSession();
        var koLang = langService.getLangOrDefault("ko");

        var response = userBusiness.updateLang(session, 1L, new UpdateLangReq(koLang.getId()));

        assertEquals("ko", response.getPreferredLang());
        assertEquals("ko", session.getPreferredLang());
    }

    @Test
    @Transactional
    void updateLang_throwsWhenLangNotFound() {
        var session = systemSession();

        assertThrows(
            ApiException.class,
            () -> userBusiness.updateLang(session, 1L, new UpdateLangReq(999999L))
        );
    }

    @Test
    @Transactional
    void updateLang_throwsWhenUserIdMismatch() {
        var session = systemSession();
        var koLang = langService.getLangOrDefault("ko");

        assertThrows(
            ApiException.class,
            () -> userBusiness.updateLang(session, 2L, new UpdateLangReq(koLang.getId()))
        );
    }
}
