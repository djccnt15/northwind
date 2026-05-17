package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "product")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductEntity extends BaseEntity {
    
    @Column(nullable = false)
    @NotNull
    private String code;
    
    @Column(nullable = false)
    @NotNull
    private String name;
    
    @Column
    private String description;
    
    @Column(name = "standard_unit_cost", nullable = false)
    @NotNull
    private Integer standardUnitCost;
    
    @Column(name = "unit_price", nullable = false)
    @NotNull
    private Integer unitPrice;
    
    @Column(name = "reorder_level", nullable = false)
    @NotNull
    private Integer reorderLevel;
    
    @Column(name = "target_level", nullable = false)
    @NotNull
    private Integer targetLevel;
    
    @Column(name = "quantity_per_unit", nullable = false)
    @NotNull
    private Integer quantityPerUnit;
    
    @Column(nullable = false)
    @NotNull
    private Boolean discontinued;
    
    @Column(name = "minimum_reorder_quantity", nullable = false)
    @NotNull
    private Integer minimumReorderQuantity;
    
    @JoinColumn(name = "product_category", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ProductCategoryEntity productCategory;
}
