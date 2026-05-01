package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupReq {
    
    public interface CreateCheck {}
    public interface ProfileUpdate {}
    public interface PasswordUpdate {}
    
    @NotBlank(
        message = "username is required value",
        groups = {CreateCheck.class, ProfileUpdate.class}
    )
    private String username;
    
    @NotBlank(
        message = "email is required value",
        groups = {CreateCheck.class, ProfileUpdate.class}
    )
    private String email;
    
    @NotBlank(
        message = "password is required value",
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    private String password;
    
    @NotBlank(
        message = "confirmPassword is required value",
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    private String confirmPassword;
}
