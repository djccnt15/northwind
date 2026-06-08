package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;
import static com.djccnt15.northwind.global.code.StatusCode.SERVER_ERROR;
import static com.djccnt15.northwind.global.exception.GlobalErrorConst.RESOURCE_NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.exception.GlobalErrorConst.UNEXPECTED_ERR_MSG;

@Slf4j
@RestControllerAdvice
@Order(Integer.MAX_VALUE)  // max value is explicit of default
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Api<?>> noResourceFound(NoResourceFoundException e) {
        log.warn("{} - {}", e.getStatusCode(), e.getMessage());

        return ResponseEntity
            .status(NOT_FOUND.getHttpStatusCode())
            .body(Api.ERROR(NOT_FOUND, messageUtil.getMessage(RESOURCE_NOT_FOUND_ERR_MSG)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Api<?>> exception(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
            .status(SERVER_ERROR.getHttpStatusCode())
            .body(Api.ERROR(SERVER_ERROR, messageUtil.getMessage(UNEXPECTED_ERR_MSG)));
    }
}
