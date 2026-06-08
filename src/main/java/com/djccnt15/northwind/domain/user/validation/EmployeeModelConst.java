package com.djccnt15.northwind.domain.user.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EmployeeModelConst {
    
    public static final int FIRST_NAME_MAX_LENGTH = 50;
    public static final int LAST_NAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int JOB_TITLE_MAX_LENGTH = 100;
    public static final int PRIMARY_PHONE_MAX_LENGTH = 25;
    public static final int SECONDARY_PHONE_MAX_LENGTH = 25;
    public static final int TITLE_OF_COURTESY_MAX_LENGTH = 25;

    public static final String FIRST_NAME_NOT_BLANK_MSG = "{validation.employee.firstName.notBlank}";
    public static final String LAST_NAME_NOT_BLANK_MSG = "{validation.employee.lastName.notBlank}";
    public static final String JOB_TITLE_NOT_BLANK_MSG = "{validation.employee.jobTitle.notBlank}";
    public static final String PRIMARY_PHONE_NOT_BLANK_MSG = "{validation.employee.primaryPhone.notBlank}";
    public static final String TITLE_OF_COURTESY_NOT_BLANK_MSG = "{validation.employee.titleOfCourtesy.notBlank}";
}
