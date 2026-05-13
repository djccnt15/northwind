package com.djccnt15.northwind.domain.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EmployeeReq {
    
    @NotBlank(message = "First name must not be blank")
    private String firstName;
    
    @NotBlank(message = "Last name must not be blank")
    private String lastName;
    
    private String email;
    
    @NotBlank(message = "Job title must not be blank")
    private String jobTitle;
    
    @NotBlank(message = "Primary phone must not be blank")
    private String primaryPhone;
    
    private String secondaryPhone;
    
    private String notes;
    
    @NotBlank(message = "Title of courtesy must not be blank")
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
