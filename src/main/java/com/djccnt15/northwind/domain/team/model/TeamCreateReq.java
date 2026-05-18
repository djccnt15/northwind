package com.djccnt15.northwind.domain.team.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.djccnt15.northwind.domain.team.validation.TeamModelConst.*;

@Data
@AllArgsConstructor
public class TeamCreateReq {
    
    @NotBlank(message = NAME_NOT_BLANK_MSG)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_LENGTH_MSG)
    private String name;
}
