package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "product_vendor")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductVendorEntity extends BaseEntity {
    
    @NotNull
    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ProductEntity product;
    
    @NotNull
    @JoinColumn(name = "vendor_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyEntity vendor;
}
