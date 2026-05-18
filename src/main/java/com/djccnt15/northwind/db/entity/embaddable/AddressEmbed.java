package com.djccnt15.northwind.db.entity.embaddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import static com.djccnt15.northwind.domain.address.validation.AddressModelConst.*;

@Data
@Embeddable
@Builder
public class AddressEmbed {
    
    @Column(length = ADDRESS_MAX_LENGTH)
    private String address;
    
    @Column(length = CITY_MAX_LENGTH)
    private String city;
    
    @Column(length = REGION_MAX_LENGTH)
    private String region;
    
    @Column(length = ZIP_CODE_MAX_LENGTH)
    private String zipCode;
    
    @Column(length = COUNTRY_MAX_LENGTH)
    private String country;
}
