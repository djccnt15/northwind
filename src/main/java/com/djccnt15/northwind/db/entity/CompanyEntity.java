package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "company", indexes = @Index(columnList = "name"))
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyEntity extends BaseEntity {
    
    @Column
    private String name;
    
    @Column(name = "business_phone")
    private String businessPhone;
    
    @Column
    private String website;
    
    @Column
    private String notes;
    
    @Column
    @Embedded
    private AddressEmbed address;
    
    @JoinColumn(name = "company_type_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyTypeEntity companyType;
    
    @JoinColumn(name = "tax_status_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TaxStatusEntity taxStatus;
}
