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

import static com.djccnt15.northwind.domain.tax.validation.TaxStatusModelConst.STATUS_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "tax_status")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaxStatusEntity extends BaseEntity {
    
    @NotNull
    @Column(length = STATUS_MAX_LENGTH, nullable = false, unique = true)
    private String status;
    
    @OneToMany(mappedBy = "taxStatus")
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude @Setter(AccessLevel.NONE)
    private Set<CompanyEntity> companies = new HashSet<>();
    
    public TaxStatusEntity(String status) {
        this.status = status;
    }
}
