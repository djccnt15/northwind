package com.djccnt15.northwind.domain.company.converter;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.ContactEntity;
import com.djccnt15.northwind.domain.company.model.ContactCreateReq;
import com.djccnt15.northwind.domain.company.model.ContactRes;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class ContactConverter {

    public ContactRes toResponse(ContactEntity entity) {
        return ContactRes.builder()
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .email(entity.getEmail())
            .jobTitle(entity.getJobTitle())
            .primaryPhone(entity.getPrimaryPhone())
            .secondaryPhone(entity.getSecondaryPhone())
            .notes(entity.getNotes())
            .companyId(entity.getCompany().getId())
            .build();
    }

    public ContactEntity toEntity(ContactCreateReq request, CompanyEntity company) {
        return ContactEntity.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .jobTitle(request.getJobTitle())
            .primaryPhone(request.getPrimaryPhone())
            .secondaryPhone(request.getSecondaryPhone())
            .notes(request.getNotes())
            .company(company)
            .build();
    }
}
