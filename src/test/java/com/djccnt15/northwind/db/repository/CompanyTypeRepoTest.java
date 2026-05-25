package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static com.djccnt15.northwind.constants.TestConst.TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class CompanyTypeRepoTest {
    
    @Autowired private CompanyTypeRepo companyTypeRepo;
    
    @Test
    void uniqueCompanyType() {
        // given
        var companyType = new CompanyTypeEntity(TEST);
        companyTypeRepo.save(companyType);
        
        // when
        var companyType2 = new CompanyTypeEntity(TEST);
        
        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> companyTypeRepo.save(companyType2)
        );
    }
}
