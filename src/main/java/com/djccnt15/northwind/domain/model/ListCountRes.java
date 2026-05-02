package com.djccnt15.northwind.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListCountRes<T> {
    
    private Integer totalCounts;
    
    private List<T> list;
}
