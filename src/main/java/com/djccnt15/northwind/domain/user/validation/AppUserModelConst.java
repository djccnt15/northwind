package com.djccnt15.northwind.domain.user.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppUserModelConst {
    
    public static final int USERNAME_MIN_LENGTH = 1;
    public static final int USERNAME_MAX_LENGTH = 25;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;
    public static final int EMAIL_MIN_LENGTH = 1;
    public static final int EMAIL_MAX_LENGTH = 100;
    
    public static final String USERNAME_BLANK_ERR_MSG = "{validation.appUser.username.notBlank}";
    public static final String USERNAME_LENGTH_ERR_MSG = "{validation.appUser.username.size}";
    public static final String PASSWORD_BLANK_ERR_MSG = "{validation.appUser.password.notBlank}";
    public static final String PASSWORD_LENGTH_ERR_MSG = "{validation.appUser.password.size}";
    public static final String EMAIL_BLANK_ERR_MSG = "{validation.appUser.email.notBlank}";
    public static final String EMAIL_LENGTH_ERR_MSG = "{validation.appUser.email.size}";
    public static final String IS_ENABLED_NULL_ERR_MSG = "{validation.appUser.isEnabled.notNull}";
    public static final String CONFIRM_PASSWORD_BLANK_ERR_MSG = "{validation.appUser.confirmPassword.notBlank}";
}
