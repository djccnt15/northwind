package com.djccnt15.northwind.domain.title.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.djccnt15.northwind.domain.title.validation.TitleModelConst.*;

@Data
@AllArgsConstructor
public class TitleCreateReq {
    
    @NotBlank(message = TITLE_NOT_BLANK_MSG)
    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH, message = TITLE_SIZE_MSG)
    private String title;
}
