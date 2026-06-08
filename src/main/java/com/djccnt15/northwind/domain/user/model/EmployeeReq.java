package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

import static com.djccnt15.northwind.domain.user.validation.EmployeeModelConst.*;

@Data
@AllArgsConstructor
public class EmployeeReq {

    @NotBlank(message = FIRST_NAME_NOT_BLANK_MSG)
    private String firstName;

    @NotBlank(message = LAST_NAME_NOT_BLANK_MSG)
    private String lastName;

    private String email;

    @NotBlank(message = JOB_TITLE_NOT_BLANK_MSG)
    private String jobTitle;

    @NotBlank(message = PRIMARY_PHONE_NOT_BLANK_MSG)
    private String primaryPhone;

    private String secondaryPhone;

    private String notes;

    @NotBlank(message = TITLE_OF_COURTESY_NOT_BLANK_MSG)
    private String titleOfCourtesy;
    
    private LocalDate birthDate;
    
    private LocalDate hireDate;
    
    private String address;
    
    private String city;
    
    private String region;
    
    private String zipCode;
    
    private String country;
    
    private byte[] photo;
}
