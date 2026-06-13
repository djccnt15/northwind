package com.djccnt15.northwind.domain.purchase.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeRef {

    private Long id;

    private String firstName;

    private String lastName;
}
