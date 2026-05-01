package com.djccnt15.northwind.exception.advice;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static com.djccnt15.northwind.comm.code.StatusCode.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
@Order(Integer.MIN_VALUE)
public class ApiExceptionHandler {
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Api<?>> apiException(ApiException apiException) {
        log.error("", apiException);
        
        var errorCode = apiException.getStatusCode();
        
        return ResponseEntity
            .status(errorCode.getHttpStatusCode())
            .body(Api.ERROR(errorCode, apiException.getDescription()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Api<?>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("", ex);
        
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage(); // 여기에 커스텀 메시지가 들어감
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity
            .status(BAD_REQUEST.getHttpStatusCode())
            .body(Api.ERROR(BAD_REQUEST, "Validation Failed", errors));
    }
}
