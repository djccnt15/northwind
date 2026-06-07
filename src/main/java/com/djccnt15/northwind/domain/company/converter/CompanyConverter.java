package com.djccnt15.northwind.domain.company.converter;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.domain.company.model.CompanyCreateReq;
import com.djccnt15.northwind.domain.company.model.CompanyRes;
import com.djccnt15.northwind.domain.tax.converter.TaxStatusConverter;
import com.djccnt15.northwind.global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
@RequiredArgsConstructor
public class CompanyConverter {

    private final CompanyTypeConverter companyTypeConverter;
    private final TaxStatusConverter taxStatusConverter;

    public CompanyRes toResponse(CompanyEntity entity) {
        var address = entity.getAddress();
        return CompanyRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .businessPhone(entity.getBusinessPhone())
            .website(entity.getWebsite())
            .notes(entity.getNotes())
            .address(address.getAddress())
            .city(address.getCity())
            .region(address.getRegion())
            .zipCode(address.getZipCode())
            .country(address.getCountry())
            .companyType(companyTypeConverter.toResponse(entity.getCompanyType()))
            .taxStatus(taxStatusConverter.toResponse(entity.getTaxStatus()))
            .build();
    }

    public CompanyEntity toEntity(CompanyCreateReq request, CompanyTypeEntity companyType, TaxStatusEntity taxStatus) {
        return CompanyEntity.builder()
            .name(request.getName())
            .businessPhone(request.getBusinessPhone())
            .website(request.getWebsite())
            .notes(request.getNotes())
            .address(toAddress(request))
            .companyType(companyType)
            .taxStatus(taxStatus)
            .build();
    }

    public AddressEmbed toAddress(CompanyCreateReq request) {
        return AddressEmbed.builder()
            .address(request.getAddress())
            .city(request.getCity())
            .region(request.getRegion())
            .zipCode(request.getZipCode())
            .country(request.getCountry())
            .build();
    }
}
