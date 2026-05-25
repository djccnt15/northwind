package com.djccnt15.northwind.domain.user.converter;

import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
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
            .address(employee.getAddress().getAddress())
            .city(employee.getAddress().getCity())
            .region(employee.getAddress().getRegion())
            .zipCode(employee.getAddress().getZipCode())
            .country(employee.getAddress().getCountry())
            .photo(employee.getPhoto())
            .title(employee.getTitle().getTitle())
            // do not return supervisor and subordinate to prevent circular reference
            .build();
    }
    
    public EmployeeEntity toEntity(EmployeeReq request, AddressEmbed address) {
        return EmployeeEntity.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .jobTitle(request.getJobTitle())
            .primaryPhone(request.getPrimaryPhone())
            .secondaryPhone(request.getSecondaryPhone())
            .notes(request.getNotes())
            .titleOfCourtesy(request.getTitleOfCourtesy())
            .birthDate(request.getBirthDate())
            .hireDate(request.getHireDate())
            .address(address)
            .photo(request.getPhoto())
            // do not set title, supervisor and subordinate to prevent circular reference
            .build();
    }
}
