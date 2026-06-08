package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.djccnt15.northwind.global.code.StatusCode.FORBIDDEN;
import static com.djccnt15.northwind.global.exception.GlobalErrorConst.ACCESS_DENIED_ERR_MSG;

@Slf4j
@RestControllerAdvice
@Order(Integer.MIN_VALUE + 1)
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Api<?>> accessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
            .status(FORBIDDEN.getHttpStatusCode())
            .body(Api.ERROR(FORBIDDEN, messageUtil.getMessage(ACCESS_DENIED_ERR_MSG)));
    }
}
