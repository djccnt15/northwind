package com.djccnt15.northwind.datacache;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class DataCacheStorage {
    
    private List<AppUserEntity> superAdmins = new ArrayList<>();
    
    /**
     * <p>data initialization or update method, called by the warmup listener or any other service that updates the data</p>
     * <p>immutable list to ensure thread safety when other services read the data</p>
     */
    public void updateData(List<AppUserEntity> newData) {
        this.superAdmins = List.copyOf(newData);
    }
}
