package com.djccnt15.northwind.domain.title.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TitleRes {
    
    private Long id;
    
    private String title;
}
