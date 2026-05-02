package com.djccnt15.northwind.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {
    
    public static String getRoleName(String role) {
        return switch (role) {
            case "SUPERADMIN", "ADMIN" -> "ADMIN";
            case null -> "USER";
            default -> role;
        };
    }
}
