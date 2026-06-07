package com.djccnt15.northwind.domain.company.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ContactModelConst {

    public static final int FIRST_NAME_MIN_LENGTH = 1;
    public static final int FIRST_NAME_MAX_LENGTH = 50;
    public static final int LAST_NAME_MIN_LENGTH = 1;
    public static final int LAST_NAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int JOB_TITLE_MAX_LENGTH = 100;
    public static final int PRIMARY_PHONE_MAX_LENGTH = 25;
    public static final int SECONDARY_PHONE_MAX_LENGTH = 25;

    public static final String FIRST_NAME_NOT_BLANK_MSG = "First name must not be blank";
    public static final String FIRST_NAME_LENGTH_MSG =
        "First name must be between " + FIRST_NAME_MIN_LENGTH + " and " + FIRST_NAME_MAX_LENGTH + " characters long";
    public static final String LAST_NAME_NOT_BLANK_MSG = "Last name must not be blank";
    public static final String LAST_NAME_LENGTH_MSG =
        "Last name must be between " + LAST_NAME_MIN_LENGTH + " and " + LAST_NAME_MAX_LENGTH + " characters long";
    public static final String EMAIL_INVALID_MSG = "Email must be a valid email address";
    public static final String EMAIL_LENGTH_MSG =
        "Email must be at most " + EMAIL_MAX_LENGTH + " characters long";
    public static final String JOB_TITLE_LENGTH_MSG =
        "Job title must be at most " + JOB_TITLE_MAX_LENGTH + " characters long";
    public static final String PRIMARY_PHONE_LENGTH_MSG =
        "Primary phone must be at most " + PRIMARY_PHONE_MAX_LENGTH + " characters long";
    public static final String SECONDARY_PHONE_LENGTH_MSG =
        "Secondary phone must be at most " + SECONDARY_PHONE_MAX_LENGTH + " characters long";
}
