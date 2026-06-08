package com.djccnt15.northwind.domain.user.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppUserErrorConst {

    public static final String NOT_FOUND_ERR_MSG = "error.user.notFound";
    public static final String UNAUTHORIZED_ACTION_ERR_MSG = "error.user.unauthorizedAction";
    public static final String PASSWORD_MISMATCH_ERR_MSG = "error.user.passwordMismatch";
    public static final String USERNAME_DUPLICATE_ERR_MSG = "error.user.usernameDuplicate";
    public static final String EMAIL_DUPLICATE_ERR_MSG = "error.user.emailDuplicate";
}
