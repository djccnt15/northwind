package com.djccnt15.northwind.domain.company.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactRes {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String jobTitle;

    private String primaryPhone;

    private String secondaryPhone;

    private String notes;

    private Long companyId;
}
