package com.djccnt15.northwind.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiTestConst {
    
    public static final String API_CODE_PATH = "$.result.code";
    public static final String API_MESSAGE_PATH = "$.result.message";
    public static final String API_DESCRIPTION_PATH = "$.result.description";
    
    public static final String API_BODY_PATH = "$.body";
}
