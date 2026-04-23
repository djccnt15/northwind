package com.djccnt15.northwind.comm.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StatusCode implements StatusCodeIfs {
    
    OK(HttpStatus.OK, 200, "Success"),
    CREATED(HttpStatus.CREATED, 201, "Created"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "Bad Request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Not Found"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal Server Error"),
    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR, 512, "Null Point");
    
    private final HttpStatus httpStatusCode;
    private final Integer statusCode;
    private final String description;
}
