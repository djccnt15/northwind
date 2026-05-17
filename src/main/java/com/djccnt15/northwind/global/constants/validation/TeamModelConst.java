package com.djccnt15.northwind.global.constants.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamModelConst {
    
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    
    public static final String NAME_LENGTH_MSG = "Team name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters long";
    public static final String NAME_NOT_BLANK_MSG = "Team name must not be blank";
}
