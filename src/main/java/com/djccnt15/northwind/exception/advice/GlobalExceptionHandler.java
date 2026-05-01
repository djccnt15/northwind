package com.djccnt15.northwind.exception.advice;

import com.djccnt15.northwind.comm.api.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.djccnt15.northwind.comm.code.StatusCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@Order(Integer.MAX_VALUE)  // max value is explicit of default
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Api<?>> exception(Exception exception) {
        log.error("", exception);
        
        return ResponseEntity
            .status(SERVER_ERROR.getHttpStatusCode())
            .body(Api.ERROR(SERVER_ERROR));
    }
}
