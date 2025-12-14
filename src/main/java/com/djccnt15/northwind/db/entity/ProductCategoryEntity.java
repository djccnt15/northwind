package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@Entity
@Table
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
    @ToString.Exclude
    private Set<ProductEntity> productEntitySet;
}
