package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.djccnt15.northwind.domain.user.validation.AppUserModelConst.LANG_ID_NULL_ERR_MSG;

@Data
@AllArgsConstructor
public class UpdateLangReq {

    public interface UpdateLang {}

    @NotNull(
        message = LANG_ID_NULL_ERR_MSG,
        groups = {UpdateLang.class}
    )
    private Long preferredLangId;
}
