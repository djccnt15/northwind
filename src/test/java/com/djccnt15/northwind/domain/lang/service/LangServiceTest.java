package com.djccnt15.northwind.domain.lang.service;

import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class LangServiceTest {

    @Autowired private LangService langService;

    @Test
    void getLangs() {
        var langs = langService.getLangs();

        assertFalse(langs.isEmpty());
        assertTrue(langs.stream().anyMatch(it -> it.getLang().equals("en")));
        assertTrue(langs.stream().anyMatch(it -> it.getLang().equals("ko")));
    }

    @Test
    void getLang() {
        var en = langService.getLangOrDefault("en");
        var found = langService.getLang(en.getId());

        assertEquals("en", found.getLang());
    }

    @Test
    void getLangNotFound() {
        assertThrows(ApiException.class, () -> langService.getLang(999999L));
    }

    @Test
    void getDefaultLang() {
        var lang = langService.getDefaultLang();

        assertEquals(LangService.DEFAULT_LANG, lang.getLang());
    }

    @Test
    void getLangOrDefaultWithValidCode() {
        var lang = langService.getLangOrDefault("ko");

        assertEquals("ko", lang.getLang());
    }

    @Test
    void getLangOrDefaultWithNull() {
        var lang = langService.getLangOrDefault(null);

        assertEquals(LangService.DEFAULT_LANG, lang.getLang());
    }

    @Test
    void getLangOrDefaultWithBlank() {
        var lang = langService.getLangOrDefault("  ");

        assertEquals(LangService.DEFAULT_LANG, lang.getLang());
    }

    @Test
    void getLangOrDefaultWithUnknownCode() {
        var lang = langService.getLangOrDefault("fr");

        assertEquals(LangService.DEFAULT_LANG, lang.getLang());
    }
}
