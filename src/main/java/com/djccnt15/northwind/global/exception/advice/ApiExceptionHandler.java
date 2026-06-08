package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static com.djccnt15.northwind.global.code.StatusCode.*;
import static com.djccnt15.northwind.global.exception.GlobalErrorConst.VALIDATION_FAILED_ERR_MSG;

@Slf4j
@RestControllerAdvice
@Order(Integer.MIN_VALUE)
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Api<?>> apiException(ApiException e) {
        var status = e.getStatusCode();
        var message = e.getMessage();
        
        if (status == UNAUTHORIZED || status == FORBIDDEN) {
            log.info("{} - {}", status, message);
        } else if (status == SERVER_ERROR) {
            log.error("{} - {}", status, message, e);
        } else {
            log.warn("{} - {}", status, message, e);
        }
        
        var errorCode = e.getStatusCode();
        return ResponseEntity
            .status(errorCode.getHttpStatusCode())
            .body(Api.ERROR(errorCode, e.getDescription()));
    }
    
    // @Valid 검증 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Api<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info(e.getMessage());
        
        var errors = new HashMap<String, String>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage(); // 유효성 검사 실패 시의 메시지
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity
            .status(VALIDATION_ERROR.getHttpStatusCode())
            .body(Api.ERROR(VALIDATION_ERROR, messageUtil.getMessage(VALIDATION_FAILED_ERR_MSG), errors));
    }
}
