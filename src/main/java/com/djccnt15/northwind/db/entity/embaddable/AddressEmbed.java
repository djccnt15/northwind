package com.djccnt15.northwind.db.entity.embaddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Embeddable
@Builder
public class AddressEmbed {
    
    @Column
    private String address;
    
    @Column
    private String city;
    
    @Column
    private String region;
    
    @Column
    private String zipCode;
    
    @Column
    private String country;
}
