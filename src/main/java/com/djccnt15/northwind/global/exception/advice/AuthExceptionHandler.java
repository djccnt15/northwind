package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.api.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.djccnt15.northwind.global.code.StatusCode.FORBIDDEN;

@Slf4j
@RestControllerAdvice
@Order(Integer.MIN_VALUE + 1)
public class AuthExceptionHandler {
    
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Api<?>> accessDeniedException(AccessDeniedException exception) {
        log.error("", exception);
        
        return ResponseEntity
            .status(FORBIDDEN.getHttpStatusCode())
            .body(Api.ERROR(FORBIDDEN, "Access Denied"));
    }
}
