package com.djccnt15.northwind.domain.company.service;

import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import com.djccnt15.northwind.db.repository.CompanyTypeRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyTypeService {

    private final CompanyTypeRepo repository;

    public List<CompanyTypeEntity> getCompanyTypes() {
        return repository.findAll(Sort.by("companyType"));
    }

    public CompanyTypeEntity getCompanyType(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Company type not found"));
    }
}
