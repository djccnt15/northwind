package com.djccnt15.northwind.domain.title.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TitleCreateReq {
    
    @NotBlank(message = "Title must not be blank")
    private String title;
}
