package com.djccnt15.northwind.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RoleConst {
    
    public static final String SUPERADMIN = "SUPERADMIN";
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
}
