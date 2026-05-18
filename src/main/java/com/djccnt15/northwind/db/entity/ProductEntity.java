package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.djccnt15.northwind.domain.product.validation.ProductModelConst.CODE_MAX_LENGTH;
import static com.djccnt15.northwind.domain.product.validation.ProductModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "product")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductEntity extends BaseEntity {
    
    @NotNull
    @Column(length = CODE_MAX_LENGTH, nullable = false, unique = true)
    private String code;
    
    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
    
    @Column
    private String description;
    
    @NotNull
    @Column(name = "standard_unit_cost", nullable = false)
    private Integer standardUnitCost;
    
    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;
    
    @NotNull
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;
    
    @NotNull
    @Column(name = "target_level", nullable = false)
    private Integer targetLevel;
    
    @NotNull
    @Column(name = "quantity_per_unit", nullable = false)
    private Integer quantityPerUnit;
    
    @NotNull
    @Column(nullable = false)
    private Boolean discontinued;
    
    @NotNull
    @Column(name = "minimum_reorder_quantity", nullable = false)
    private Integer minimumReorderQuantity;
    
    @NotNull
    @JoinColumn(name = "product_category", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ProductCategoryEntity productCategory;
}
