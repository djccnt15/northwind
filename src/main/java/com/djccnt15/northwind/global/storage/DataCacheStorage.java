package com.djccnt15.northwind.global.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Component
public class DataCacheStorage {
    
    private Set<Long> superAdmins = new HashSet<>();
    
    /**
     * <p>data initialization or update method, called by the warmup listener or any other service that updates the data</p>
     * <p>immutable list to ensure thread safety when other services read the data</p>
     */
    public void updateData(List<Long> newData) {
        this.superAdmins = Set.copyOf(newData);
    }
}
