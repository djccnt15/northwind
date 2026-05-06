package com.djccnt15.northwind.global.util;

import lombok.experimental.UtilityClass;

import static com.djccnt15.northwind.global.constants.RoleConst.*;

@UtilityClass
public class UserUtil {
    
    public static String getRoleName(String role) {
        return switch (role) {
            case SUPERADMIN, ADMIN -> ADMIN;
            case null -> USER;
            default -> role;
        };
    }
}
