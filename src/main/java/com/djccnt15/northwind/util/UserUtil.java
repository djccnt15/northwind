package com.djccnt15.northwind.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {
    
    public static String getRoleName(String role) {
        return switch (role) {
            case "superadmin", "admin" -> "admin";
            case null -> "user";
            default -> role;
        };
    }
}
