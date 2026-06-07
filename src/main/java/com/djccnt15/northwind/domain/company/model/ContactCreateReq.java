package com.djccnt15.northwind.domain.company.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.djccnt15.northwind.domain.company.validation.ContactModelConst.*;

@Data
@AllArgsConstructor
public class ContactCreateReq {

    @NotBlank(message = FIRST_NAME_NOT_BLANK_MSG)
    @Size(min = FIRST_NAME_MIN_LENGTH, max = FIRST_NAME_MAX_LENGTH, message = FIRST_NAME_LENGTH_MSG)
    private String firstName;

    @NotBlank(message = LAST_NAME_NOT_BLANK_MSG)
    @Size(min = LAST_NAME_MIN_LENGTH, max = LAST_NAME_MAX_LENGTH, message = LAST_NAME_LENGTH_MSG)
    private String lastName;

    @Email(message = EMAIL_INVALID_MSG)
    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_LENGTH_MSG)
    private String email;

    @Size(max = JOB_TITLE_MAX_LENGTH, message = JOB_TITLE_LENGTH_MSG)
    private String jobTitle;

    @Size(max = PRIMARY_PHONE_MAX_LENGTH, message = PRIMARY_PHONE_LENGTH_MSG)
    private String primaryPhone;

    @Size(max = SECONDARY_PHONE_MAX_LENGTH, message = SECONDARY_PHONE_LENGTH_MSG)
    private String secondaryPhone;

    private String notes;
}
