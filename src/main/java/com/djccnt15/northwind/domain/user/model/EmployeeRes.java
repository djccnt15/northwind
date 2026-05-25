package com.djccnt15.northwind.domain.user.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class EmployeeRes {
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String jobTitle;
    
    private String primaryPhone;
    
    private String secondaryPhone;
    
    private String notes;
    
    private String titleOfCourtesy;
    
    private LocalDate birthDate;
    
    private LocalDate hireDate;
    
    private String address;
    
    private String city;
    
    private String region;
    
    private String zipCode;
    
    private String country;
    
    private byte[] photo;
    
    private String title;
    
    private EmployeeRes supervisor;
    
    @Builder.Default
    private Set<EmployeeRes> subordinates = new HashSet<>();
}
