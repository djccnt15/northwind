package com.djccnt15.northwind.domain.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoRes {
    
    private Long id;
    
    private String username;
}
