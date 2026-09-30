package com.djccnt15.northwind.global.exception.advice;

import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;

import static com.djccnt15.northwind.global.code.StatusCode.*;
import static com.djccnt15.northwind.global.exception.GlobalErrorConst.*;

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
    
    /**
     * 유효성 검사 실패 시 {@code 1400}. 필드별 오류 메시지를 반환한다.
     */
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
    
    /** 경로 변수·쿼리 파라미터 타입 변환 실패(예: {@code /orders/abc})는 {@code 400}. 파라미터 이름을 덧붙인다. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Api<?>> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("{} - {}={}", BAD_REQUEST, ex.getName(), ex.getValue());
        
        return ResponseEntity
            .status(BAD_REQUEST.getHttpStatusCode())
            .body(Api.ERROR(BAD_REQUEST, messageUtil.getMessage(INVALID_PARAMETER_ERR_MSG, ex.getName())));
    }
    
    /** 본문 역직렬화 실패(잘못된 enum 문자열, 타입 불일치, 깨진 JSON, 본문 누락)는 {@code 400}. 필드를 특정할 수 있으면 경로를 덧붙인다. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Api<?>> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("{} - {}", BAD_REQUEST, exception.getMessage());
        
        var field = fieldPath(exception);
        var description = field == null
            ? messageUtil.getMessage(INVALID_REQUEST_BODY_ERR_MSG)
            : messageUtil.getMessage(INVALID_REQUEST_FIELD_ERR_MSG, field);
        return ResponseEntity
            .status(BAD_REQUEST.getHttpStatusCode())
            .body(Api.ERROR(BAD_REQUEST, description));
    }
    
    /** 값 불일치({@link MismatchedInputException})일 때만 {@code role}, {@code leaderTeamIds[1]} 형태의 경로. 문법 오류·본문 누락은 {@code null}. */
    private static String fieldPath(HttpMessageNotReadableException exception) {
        if (!(exception.getCause() instanceof MismatchedInputException cause)) {
            return null;
        }
        
        var path = new StringBuilder();
        for (var ref : cause.getPath()) {
            if (ref.getFieldName() != null) {
                if (!path.isEmpty()) {
                    path.append('.');
                }
                path.append(ref.getFieldName());
            } else if (ref.getIndex() >= 0) {
                path.append('[').append(ref.getIndex()).append(']');
            }
        }
        return path.isEmpty() ? null : path.toString();
    }
}
