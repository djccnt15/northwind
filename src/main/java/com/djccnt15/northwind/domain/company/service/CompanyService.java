package com.djccnt15.northwind.domain.company.service;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.db.repository.CompanyRepo;
import com.djccnt15.northwind.domain.company.converter.CompanyConverter;
import com.djccnt15.northwind.domain.company.model.CompanyCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepo repository;
    private final CompanyConverter converter;

    public Page<CompanyEntity> getCompanies(String kw, Long typeId, Pageable pageable) {
        return repository.findByFilter(kw, typeId, pageable);
    }

    public CompanyEntity getCompany(Long id) {
        return repository.findWithRelationById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Company not found"));
    }

    public void validateCompany(CompanyCreateReq request) {
        if (repository.existsByName(request.getName())) {
            throw new ApiException(BAD_REQUEST, "Company name already exists");
        }
    }

    public void validateCompany(Long id, CompanyCreateReq request) {
        if (repository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ApiException(BAD_REQUEST, "Company name already exists");
        }
    }

    public CompanyEntity createCompany(
        CompanyCreateReq request, CompanyTypeEntity companyType, TaxStatusEntity taxStatus
    ) {
        var entity = converter.toEntity(request, companyType, taxStatus);
        repository.save(entity);
        return entity;
    }

    public CompanyEntity updateCompany(
        CompanyEntity entity, CompanyCreateReq request, CompanyTypeEntity companyType, TaxStatusEntity taxStatus
    ) {
        entity.setName(request.getName());
        entity.setBusinessPhone(request.getBusinessPhone());
        entity.setWebsite(request.getWebsite());
        entity.setNotes(request.getNotes());
        entity.setAddress(converter.toAddress(request));
        entity.setCompanyType(companyType);
        entity.setTaxStatus(taxStatus);
        repository.save(entity);
        return entity;
    }

    public void deleteCompany(CompanyEntity entity) {
        repository.delete(entity);
    }
}
