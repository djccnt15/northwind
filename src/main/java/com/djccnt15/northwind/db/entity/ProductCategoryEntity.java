package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.product.validation.ProductCategoryModelConst.CODE_MAX_LENGTH;
import static com.djccnt15.northwind.domain.product.validation.ProductCategoryModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "product_category")
@ToString(callSuper = true)
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
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String description;
    
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.REMOVE)
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<ProductEntity> productEntitySet = new HashSet<>();
}
