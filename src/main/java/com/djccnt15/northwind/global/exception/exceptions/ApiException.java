package com.djccnt15.northwind.global.exception.exceptions;

import com.djccnt15.northwind.global.code.StatusCodeIfs;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    
    private final StatusCodeIfs statusCode;
    private final String description;
    
    
    public ApiException(StatusCodeIfs statusCode) {
        super(statusCode.getDescription());
        this.statusCode = statusCode;
        this.description = statusCode.getDescription();
    }
    
    public ApiException(StatusCodeIfs statusCode, String description) {
        super(description);
        this.statusCode = statusCode;
        this.description = description;
    }
    
    public ApiException(StatusCodeIfs statusCode, Throwable tx) {
        super(tx);
        this.statusCode = statusCode;
        this.description = statusCode.getDescription();
    }
    
    public ApiException(StatusCodeIfs statusCode, Throwable tx, String description) {
        super(tx);
        this.statusCode = statusCode;
        this.description = description;
    }
}
