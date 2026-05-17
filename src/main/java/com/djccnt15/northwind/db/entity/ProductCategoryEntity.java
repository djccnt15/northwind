package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.global.constants.validation.ProductCategoryModelConst.CODE_MAX_LENGTH;
import static com.djccnt15.northwind.global.constants.validation.ProductCategoryModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "product_category")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductCategoryEntity extends BaseEntity {
    
    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
    
    @NotNull
    @Column(length = CODE_MAX_LENGTH, nullable = false, unique = true)
    private String code;
    
    @Column
    private String description;
    
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude @Setter(AccessLevel.NONE)
    private Set<ProductEntity> productEntitySet = new HashSet<>();
}
