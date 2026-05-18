package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static com.djccnt15.northwind.domain.user.validation.AppUserModelConst.*;

@Data
@AllArgsConstructor
public class SignupReq {
    
    public interface CreateCheck {}
    public interface ProfileUpdate {}
    public interface PasswordUpdate {}
    public interface AdminUpdate {}
    
    @NotBlank(
        message = USERNAME_BLANK_ERR_MSG,
        groups = {CreateCheck.class, ProfileUpdate.class, AdminUpdate.class}
    )
    @Size(
        min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = USERNAME_LENGTH_ERR_MSG,
        groups = {CreateCheck.class, ProfileUpdate.class, AdminUpdate.class}
    )
    private String username;
    
    @NotBlank(
        message = PASSWORD_BLANK_ERR_MSG,
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    @Size(
        min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_LENGTH_ERR_MSG,
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    private String password;
    
    @NotBlank(
        message = EMAIL_BLANK_ERR_MSG,
        groups = {CreateCheck.class, ProfileUpdate.class, AdminUpdate.class}
    )
    @Size(
        min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH, message = EMAIL_LENGTH_ERR_MSG,
        groups = {CreateCheck.class, ProfileUpdate.class, AdminUpdate.class}
    )
    private String email;
    
    @NotNull(
        message = IS_ENABLED_NULL_ERR_MSG,
        groups = {AdminUpdate.class}
    )
    private boolean isEnabled;
    
    private LocalDateTime liveUntil;
    
    @NotBlank(
        message = CONFIRM_PASSWORD_BLANK_ERR_MSG,
        groups = {CreateCheck.class, PasswordUpdate.class}
    )
    private String confirmPassword;
    
    private String team;
}
