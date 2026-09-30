package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.message.MessageUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "errors");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        handler = new ApiExceptionHandler(new MessageUtil(messageSource));
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void methodArgumentTypeMismatch_localizedWithParameterName() {
        var ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("orderId");

        LocaleContextHolder.setLocale(Locale.KOREAN);
        var ko = handler.methodArgumentTypeMismatchException(ex);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        var en = handler.methodArgumentTypeMismatchException(ex);

        assertThat(ko.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ko.getBody().getResult().getDescription()).isEqualTo("요청 파라미터가 올바르지 않습니다: orderId");
        assertThat(en.getBody().getResult().getDescription()).isEqualTo("The request parameter is invalid: orderId");
    }

    @Test
    void httpMessageNotReadable_withFieldPath_localizedWithField() {
        var cause = InvalidFormatException.from(null, "bad enum", "NOPE", String.class);
        cause.prependPath(new Object(), 1);
        cause.prependPath(new Object(), "leaderTeamIds");
        var ex = new HttpMessageNotReadableException("bad", cause, mock(HttpInputMessage.class));

        LocaleContextHolder.setLocale(Locale.KOREAN);
        var ko = handler.httpMessageNotReadableException(ex);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        var en = handler.httpMessageNotReadableException(ex);

        assertThat(ko.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ko.getBody().getResult().getDescription()).isEqualTo("요청한 데이터가 올바르지 않습니다: leaderTeamIds[1]");
        assertThat(en.getBody().getResult().getDescription()).isEqualTo("The request data is invalid: leaderTeamIds[1]");
    }

    @Test
    void httpMessageNotReadable_withoutFieldPath_localizedGenericMessage() {
        var ex = new HttpMessageNotReadableException(
            "bad", new JsonParseException(null, "malformed"), mock(HttpInputMessage.class));

        LocaleContextHolder.setLocale(Locale.KOREAN);
        var ko = handler.httpMessageNotReadableException(ex);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        var en = handler.httpMessageNotReadableException(ex);

        assertThat(ko.getBody().getResult().getDescription()).isEqualTo("요청한 데이터가 올바르지 않습니다");
        assertThat(en.getBody().getResult().getDescription()).isEqualTo("The request data is invalid");
    }
}
