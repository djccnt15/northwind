package com.djccnt15.northwind.comm.api;

import com.djccnt15.northwind.comm.code.StatusCode;
import com.djccnt15.northwind.comm.code.StatusCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    
    private Integer code;
    private String message;
    private String description;
    
    public static Result OK() {
        return Result.builder()
            .code(StatusCode.OK.getStatusCode())
            .message(StatusCode.OK.getDescription())
            .description("OK")
            .build();
    }
    
    public static Result CREATED() {
        return Result.builder()
            .code(StatusCode.CREATED.getStatusCode())
            .message(StatusCode.CREATED.getDescription())
            .description("CREATED")
            .build();
    }
    
    public static Result ERROR(StatusCodeIfs statusCodeIfs) {
        return Result.builder()
            .code(statusCodeIfs.getStatusCode())
            .message(statusCodeIfs.getDescription())
            .description("ERROR")
            .build();
    }
    
    public static Result ERROR(StatusCodeIfs statusCodeIfs, Throwable tx) {
        return Result.builder()
            .code(statusCodeIfs.getStatusCode())
            .message(statusCodeIfs.getDescription())
            .description(tx.getLocalizedMessage())
            .build();
    }
    
    public static Result ERROR(StatusCodeIfs statusCodeIfs, String description) {
        return Result.builder()
            .code(statusCodeIfs.getStatusCode())
            .message(statusCodeIfs.getDescription())
            .description(description)
            .build();
    }
}
