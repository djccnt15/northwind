package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.db.repository.EmployeeRepo;
import com.djccnt15.northwind.db.repository.TitleRepo;
import com.djccnt15.northwind.domain.address.converter.AddressConverter;
import com.djccnt15.northwind.domain.user.converter.EmployeeConverter;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.djccnt15.northwind.global.code.StatusCode.SERVER_ERROR;
import static com.djccnt15.northwind.domain.user.validation.EmployeeErrorConst.TITLE_REFERENCE_NOT_FOUND_ERR_MSG;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepo repository;
    private final EmployeeConverter converter;
    private final AddressConverter addressConverter;
    private final TitleRepo titleRepo;
    private final MessageUtil messageUtil;

    public EmployeeEntity createEmployee(EmployeeReq request, AppUserEntity userEntity) {
        var address = addressConverter.toEmbed(request);
        var entity = converter.toEntity(request, address);
        var title = titleRepo.findById(1L)
            .orElseThrow(() -> new ApiException(SERVER_ERROR, messageUtil.getMessage(TITLE_REFERENCE_NOT_FOUND_ERR_MSG)));
        entity.setTitle(title);
        entity.setAppUser(userEntity);
        return repository.save(entity);
    }

    public EmployeeEntity createEmployee(SignupReq request, AppUserEntity userEntity) {
        var entity = new EmployeeEntity();
        var title = titleRepo.findFirstByTitle(request.getTitle())
            .orElseThrow(() -> new ApiException(SERVER_ERROR, messageUtil.getMessage(TITLE_REFERENCE_NOT_FOUND_ERR_MSG)));
        entity.setAppUser(userEntity);
        entity.setTitle(title);
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
