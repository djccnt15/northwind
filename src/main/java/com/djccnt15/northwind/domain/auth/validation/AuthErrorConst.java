package com.djccnt15.northwind.domain.auth.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthErrorConst {

    public static final String AUTHENTICATION_REQUIRED_ERR_MSG = "error.auth.authenticationRequired";
    public static final String ACCESS_DENIED_ERR_MSG = "error.auth.accessDenied";
    public static final String BAD_CREDENTIALS_ERR_MSG = "error.auth.badCredentials";
    public static final String ACCOUNT_DISABLED_ERR_MSG = "error.auth.accountDisabled";
    public static final String ACCOUNT_LOCKED_ERR_MSG = "error.auth.accountLocked";
    public static final String ACCOUNT_EXPIRED_ERR_MSG = "error.auth.accountExpired";
    public static final String CREDENTIALS_EXPIRED_ERR_MSG = "error.auth.credentialsExpired";
    public static final String CONTACT_ADMINISTRATOR_ERR_MSG = "error.auth.contactAdministrator";
}
