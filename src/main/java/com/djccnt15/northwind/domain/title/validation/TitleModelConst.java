package com.djccnt15.northwind.domain.title.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TitleModelConst {
    
    public static final int TITLE_MIN_LENGTH = 1;
    public static final int TITLE_MAX_LENGTH = 20;
    
    public static final String TITLE_NOT_BLANK_MSG = "{validation.title.notBlank}";
    public static final String TITLE_SIZE_MSG = "{validation.title.size}";
}
