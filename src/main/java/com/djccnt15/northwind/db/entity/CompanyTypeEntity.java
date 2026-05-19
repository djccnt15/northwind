package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.company.validation.CompanyTypeModelConst.COMPANY_TYPE_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "company_type")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyTypeEntity extends BaseEntity {
    
    @NotNull
    @Column(name = "company_type", length = COMPANY_TYPE_MAX_LENGTH, nullable = false, unique = true)
    private String companyType;
    
    @OneToMany(mappedBy = "companyType")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<CompanyEntity> companies = new HashSet<>();
    
    public CompanyTypeEntity (String companyType) {
        this.companyType = companyType;
    }
}
