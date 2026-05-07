package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.api.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;
import static com.djccnt15.northwind.global.code.StatusCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@Order(Integer.MAX_VALUE)  // max value is explicit of default
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Api<?>> noResourceFound(NoResourceFoundException exception) {
        log.error("", exception);
        
        return ResponseEntity
            .status(NOT_FOUND.getHttpStatusCode())
            .body(Api.ERROR(NOT_FOUND, "Resource not found"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Api<?>> exception(Exception exception) {
        log.error("", exception);
        
        return ResponseEntity
            .status(SERVER_ERROR.getHttpStatusCode())
            .body(Api.ERROR(SERVER_ERROR));
    }
}
