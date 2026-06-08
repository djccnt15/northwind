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

    public static final String FIRST_NAME_NOT_BLANK_MSG = "{validation.contact.firstName.notBlank}";
    public static final String FIRST_NAME_LENGTH_MSG = "{validation.contact.firstName.size}";
    public static final String LAST_NAME_NOT_BLANK_MSG = "{validation.contact.lastName.notBlank}";
    public static final String LAST_NAME_LENGTH_MSG = "{validation.contact.lastName.size}";
    public static final String EMAIL_INVALID_MSG = "{validation.contact.email.invalid}";
    public static final String EMAIL_LENGTH_MSG = "{validation.contact.email.size}";
    public static final String JOB_TITLE_LENGTH_MSG = "{validation.contact.jobTitle.size}";
    public static final String PRIMARY_PHONE_LENGTH_MSG = "{validation.contact.primaryPhone.size}";
    public static final String SECONDARY_PHONE_LENGTH_MSG = "{validation.contact.secondaryPhone.size}";
}
