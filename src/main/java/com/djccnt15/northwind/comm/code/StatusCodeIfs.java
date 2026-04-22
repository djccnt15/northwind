package com.djccnt15.northwind.comm.code;

import org.springframework.http.HttpStatus;

public interface StatusCodeIfs {
    
    HttpStatus getHttpStatusCode();  // http status code
    Integer getStatusCode();  // internal error code
    String getDescription();
}
