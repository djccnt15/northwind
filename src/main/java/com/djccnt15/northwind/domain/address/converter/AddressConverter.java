package com.djccnt15.northwind.domain.address.converter;

import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.domain.user.model.AddressRes;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import com.djccnt15.northwind.global.annotation.Converter;

@Converter
public class AddressConverter {
    
    public AddressRes toResponse(AddressEmbed embedded) {
        return AddressRes.builder()
            .address(embedded.getAddress())
            .city(embedded.getCity())
            .region(embedded.getRegion())
            .zipCode(embedded.getZipCode())
            .country(embedded.getCountry())
            .build();
    }
    
    public AddressEmbed toEmbed(EmployeeReq response) {
        return AddressEmbed.builder()
            .address(response.getAddress())
            .city(response.getCity())
            .region(response.getRegion())
            .zipCode(response.getZipCode())
            .country(response.getCountry())
            .build();
    }
}
