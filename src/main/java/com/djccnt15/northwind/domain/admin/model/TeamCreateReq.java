package com.djccnt15.northwind.domain.admin.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamCreateReq {
    
    @NotBlank(message = "Team name must not be blank")
    private String name;
}
