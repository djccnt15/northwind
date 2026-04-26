package com.djccnt15.northwind.domain.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupReq {
    
    private String username;
    
    private String email;
    
    private String password;
    
    private String confirmPassword;
}
