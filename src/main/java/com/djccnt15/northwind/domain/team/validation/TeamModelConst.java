package com.djccnt15.northwind.domain.team.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamModelConst {
    
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    
    public static final String NAME_LENGTH_MSG = "{validation.team.name.size}";
    public static final String NAME_NOT_BLANK_MSG = "{validation.team.name.notBlank}";
}
