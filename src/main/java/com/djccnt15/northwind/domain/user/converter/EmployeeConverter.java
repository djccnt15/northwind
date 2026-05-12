package com.djccnt15.northwind.domain.user.converter;

import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.domain.user.model.EmployeeRes;
import com.djccnt15.northwind.global.annotation.Converter;

@Converter
public class EmployeeConverter {
    
    public EmployeeRes toResponse(EmployeeEntity employee) {
        return EmployeeRes.builder()
            .firstName(employee.getFirstName())
            .lastName(employee.getLastName())
            .email(employee.getEmail())
            .JobTitle(employee.getJobTitle())
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
}
