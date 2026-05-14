package com.djccnt15.northwind.domain.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressRes {
    
    private String address;
    
    private String city;
    
    private String region;
    
    private String zipCode;
    
    private String country;
}
