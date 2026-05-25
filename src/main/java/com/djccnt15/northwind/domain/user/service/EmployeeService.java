package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.db.repository.EmployeeRepo;
import com.djccnt15.northwind.domain.address.converter.AddressConverter;
import com.djccnt15.northwind.domain.user.converter.EmployeeConverter;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepo repository;
    private final EmployeeConverter converter;
    private final AddressConverter addressConverter;
    
    public EmployeeEntity createEmployee(EmployeeReq request) {
        var address = addressConverter.toEmbed(request);
        var entity = converter.toEntity(request, address);
        return repository.save(entity);
    }
    
    public Optional<EmployeeEntity> getEmployee(AppUserEntity user) {
        return repository.findFistByAppUser(user);
    }
    
    public EmployeeEntity updateEmployee(EmployeeEntity employee, EmployeeReq request) {
        var address = addressConverter.toEmbed(request);
        
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setJobTitle(request.getJobTitle());
        employee.setPrimaryPhone(request.getPrimaryPhone());
        employee.setSecondaryPhone(request.getSecondaryPhone());
        employee.setTitleOfCourtesy(request.getTitleOfCourtesy());
        employee.setBirthDate(request.getBirthDate());
        employee.setAddress(address);
        return repository.save(employee);
    }
}
