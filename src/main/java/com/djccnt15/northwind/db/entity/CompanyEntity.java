package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.company.validation.CompanyModelConst.*;

@Getter
@Setter
@Entity
@Table(name = "company", indexes = @Index(columnList = "name"))
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyEntity extends BaseEntity {
    
    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false)
    private String name;
    
    @Column(name = "business_phone", length = BUSINESS_PHONE_MAX_LENGTH)
    private String businessPhone;
    
    @Column(length = WEBSITE_MAX_LENGTH)
    private String website;
    
    @Column
    private String notes;
    
    @Column
    @Embedded
    private AddressEmbed address;
    
    @NotNull
    @JoinColumn(name = "company_type_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyTypeEntity companyType;
    
    @NotNull
    @JoinColumn(name = "tax_status_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TaxStatusEntity taxStatus;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<ContactEntity> contacts = new HashSet<>();
}
