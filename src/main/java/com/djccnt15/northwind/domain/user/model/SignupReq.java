package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SignupReq {
    
    public interface CreateCheck {}
    public interface ProfileUpdate {}
    public interface PasswordUpdate {}
    public interface AdminUpdate {}
    
    @NotBlank(
        message = "username is required value",
        groups = {CreateCheck.class, ProfileUpdate.class, AdminUpdate.class}
    )
    private String username;
    
    @NotBlank(
        message = "password is required value",
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    private String password;
    
    @NotBlank(
        message = "email is required value",
        groups = {CreateCheck.class, ProfileUpdate.class, AdminUpdate.class}
    )
    private String email;
    
    @NotNull(
        message = "isEnabled is required value",
        groups = {AdminUpdate.class}
    )
    private boolean isEnabled;
    
    private LocalDateTime liveUntil;
    
    @NotBlank(
        message = "confirmPassword is required value",
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    private String confirmPassword;
}
