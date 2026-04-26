package com.djccnt15.northwind.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RouteConst {
    
    public static final String API = "/api";
    public static final String PUBLIC = "/public";
    public static final String ALL = "/**";
    
    public static final String API_ALL = API + ALL;
    public static final String PUBLIC_API = API + PUBLIC;
    public static final String PUBLIC_ALL = PUBLIC_API + ALL;
    
    public static final String V1 = "/v1";
    
    public static final String API_V1 = API + V1;
    public static final String PUBLIC_API_V1 = PUBLIC_API + V1;
    
    public static final String[] PUBLIC_PATHS = {
        "/",
        "/index.html",
        "/statics/**",
        "/assets/**",
        "/css/**",
        "/favicon.*",
        "/*.ico"
    };
    
    public static final String[] SESSION_CHECK_PATHS = {
        PUBLIC_API_V1 + "/auth/check-session"
    };
    
    public static final String[] SWAGGER_PATHS = {
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
}
