package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupReq {
    
    @NotBlank(message = "username is required value")
    private String username;
    
    @NotBlank(message = "email is required value")
    private String email;
    
    @NotBlank(message = "password is required value")
    private String password;
    
    @NotBlank(message = "confirmPassword is required value")
    private String confirmPassword;
}
