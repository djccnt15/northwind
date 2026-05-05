package com.djccnt15.northwind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListBodyReq<T> {
    
    private List<T> list;
}
