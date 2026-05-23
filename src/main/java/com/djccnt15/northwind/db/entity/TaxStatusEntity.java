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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaxStatusEntity extends BaseEntity {
    
    @NotNull
    @Column(length = STATUS_MAX_LENGTH, nullable = false, unique = true)
    private String status;
    
    @OneToMany(mappedBy = "taxStatus")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<CompanyEntity> companies = new HashSet<>();
    
    @OneToMany(mappedBy = "taxStatus")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<OrdersEntity> orders = new HashSet<>();
    
    public TaxStatusEntity(String status) {
        this.status = status;
    }
}
