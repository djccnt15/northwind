package com.djccnt15.northwind.global.schedule;

import com.djccnt15.northwind.global.storage.DataCacheStorage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class DataLoaderTest {
    
    @Autowired private DataLoader dataLoader;
    @Autowired private DataCacheStorage dataCacheStorage;
    
    @Test
    void refreshSuperAdminCache() {
        assertDoesNotThrow(() -> dataLoader.refreshSuperAdminCache());
        
        var superAdmins = dataCacheStorage.getSuperAdmins();
        assertNotNull(superAdmins);
    }
}
