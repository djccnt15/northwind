package com.djccnt15.northwind.domain.user.converter;

import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import com.djccnt15.northwind.domain.user.model.EmployeeRes;
import com.djccnt15.northwind.global.annotation.Converter;

@Converter
public class EmployeeConverter {
    
    public EmployeeRes toResponse(EmployeeEntity employee) {
        return EmployeeRes.builder()
            .firstName(employee.getFirstName())
            .lastName(employee.getLastName())
            .email(employee.getEmail())
            .jobTitle(employee.getJobTitle())
            .primaryPhone(employee.getPrimaryPhone())
            .secondaryPhone(employee.getSecondaryPhone())
            .notes(employee.getNotes())
            .titleOfCourtesy(employee.getTitleOfCourtesy())
            .birthDate(employee.getBirthDate())
            .hireDate(employee.getHireDate())
            .address(employee.getAddress())
            .city(employee.getCity())
            .region(employee.getRegion())
            .zipCode(employee.getZipCode())
            .country(employee.getCountry())
            .photo(employee.getPhoto())
            .title(employee.getTitle().getTitle())
            // do not return supervisor and subordinate to prevent circular reference
            .build();
    }
    
    public EmployeeEntity toEntity(EmployeeReq response) {
        return EmployeeEntity.builder()
            .firstName(response.getFirstName())
            .lastName(response.getLastName())
            .email(response.getEmail())
            .jobTitle(response.getJobTitle())
            .primaryPhone(response.getPrimaryPhone())
            .secondaryPhone(response.getSecondaryPhone())
            .notes(response.getNotes())
            .titleOfCourtesy(response.getTitleOfCourtesy())
            .birthDate(response.getBirthDate())
            .hireDate(response.getHireDate())
            .address(response.getAddress())
            .city(response.getCity())
            .region(response.getRegion())
            .zipCode(response.getZipCode())
            .country(response.getCountry())
            .photo(response.getPhoto())
            // do not set title, supervisor and subordinate to prevent circular reference
            .build();
    }
}
