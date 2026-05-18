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
    
    public static final String USERNAME_BLANK_ERR_MSG = "username is required value";
    public static final String USERNAME_LENGTH_ERR_MSG = "username must be between " + USERNAME_MIN_LENGTH + " and " + USERNAME_MAX_LENGTH + " characters";
    public static final String USERNAME_DUPLICATE_ERR_MSG = "username is already taken";
    public static final String PASSWORD_BLANK_ERR_MSG = "password is required value";
    public static final String PASSWORD_LENGTH_ERR_MSG = "password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters";
    public static final String EMAIL_BLANK_ERR_MSG = "email is required value";
    public static final String EMAIL_LENGTH_ERR_MSG = "email must be between " + EMAIL_MIN_LENGTH + " and " + EMAIL_MAX_LENGTH + " characters";
    public static final String EMAIL_DUPLICATE_ERR_MSG = "email is already taken";
    public static final String IS_ENABLED_NULL_ERR_MSG = "isEnabled is required value";
    public static final String CONFIRM_PASSWORD_BLANK_ERR_MSG = "confirmPassword is required value";
}
