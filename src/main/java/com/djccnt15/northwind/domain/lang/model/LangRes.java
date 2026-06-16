package com.djccnt15.northwind.domain.lang.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LangRes {

    private Long id;

    private String lang;
}
