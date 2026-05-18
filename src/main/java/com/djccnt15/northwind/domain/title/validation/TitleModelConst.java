package com.djccnt15.northwind.domain.title.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TitleModelConst {
    
    public static final int TITLE_MIN_LENGTH = 1;
    public static final int TITLE_MAX_LENGTH = 20;
    
    public static final String TITLE_NOT_BLANK_MSG = "Title must not be blank";
    public static final String TITLE_SIZE_MSG = "Title must be between " + TITLE_MIN_LENGTH + " and " + TITLE_MAX_LENGTH + " characters long";
}
