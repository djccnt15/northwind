package com.djccnt15.northwind.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RouteConst {
    
    public static final String API_VER_1 = "/api/v1";
    
    public static final String[] PUBLIC_PATHS = {
        "/",
        "/index.html",
        "/statics/**",
        "/assets/**",
        "/css/**",
        "/favicon.*",
        "/*.ico"
    };
    
    public static final String[] PUBLIC_AUTH_PATHS = {
        API_VER_1 + "/login",
        API_VER_1 + "/signup",
        API_VER_1 + "/auth/login/fail",
        API_VER_1 + "/auth/logout"
    };
    
    public static final String[] SESSION_CHECK_PATHS = {
        API_VER_1 + "/auth/check-session"
    };
    
    public static final String[] SWAGGER_PATHS = {
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
}
