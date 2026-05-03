package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "product_category")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductCategoryEntity extends BaseEntity {
    
    @Column(nullable = false)
    @NotNull
    private String name;
    
    @Column(nullable = false)
    @NotNull
    private String code;
    
    @Column
    private String description;
    
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<ProductEntity> productEntitySet = new HashSet<>();
}
