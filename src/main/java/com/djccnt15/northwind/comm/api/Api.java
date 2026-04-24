package com.djccnt15.northwind.comm.api;

import com.djccnt15.northwind.comm.code.StatusCodeIfs;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.System.currentTimeMillis;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Api<T> {
    
    private long serverTime;  // Epoch Millis
    
    private Result result;
    
    @Valid  // recursive validation for actual body data object
    private T body;
    
    public static <T> Api<T> OK(T data) {
        return new Api<>(currentTimeMillis(), Result.OK(), data);
    }
    
    public static <T> Api<T> CREATED(T data) {
        return new Api<>(currentTimeMillis(), Result.CREATED(), data);
    }
    
    public static Api<Object> ERROR(Result result) {
        var api = new Api<Object>();
        api.serverTime = currentTimeMillis();
        api.result = result;
        return api;
    }
    
    public static Api<Object> ERROR(StatusCodeIfs statusCodeIfs) {
        var api = new Api<Object>();
        api.serverTime = currentTimeMillis();
        api.result = Result.ERROR(statusCodeIfs);
        return api;
    }
    
    public static Api<Object> ERROR(StatusCodeIfs statusCodeIfs, Throwable tx) {
        var api = new Api<Object>();
        api.serverTime = currentTimeMillis();
        api.result = Result.ERROR(statusCodeIfs, tx);
        return api;
    }
    
    public static Api<Object> ERROR(StatusCodeIfs statusCodeIfs, String description) {
        var api = new Api<Object>();
        api.serverTime = currentTimeMillis();
        api.result = Result.ERROR(statusCodeIfs, description);
        return api;
    }
}
