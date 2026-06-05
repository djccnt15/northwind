package com.djccnt15.northwind.domain.product.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRes {
    
    private Long id;
    
    private String code;
    
    private String name;
    
    private String description;
    
    private BigDecimal standardUnitCost;
    
    private BigDecimal unitPrice;
    
    private Integer reorderLevel;
    
    private Integer targetLevel;
    
    private Integer quantityPerUnit;
    
    private Integer minimumReorderQuantity;
    
    private Boolean discontinued;
    
    private ProductCategoryRes category;
}
