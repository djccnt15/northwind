package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static com.djccnt15.northwind.constants.TestConst.TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class TaxStatusRepoTest {
    
    @Autowired private TaxStatusRepo taxStatusRepo;
    
    @Test
    void uniqueTaxStatus() {
        // given
        var newTaxStatus = new TaxStatusEntity(TEST);
        taxStatusRepo.save(newTaxStatus);
        
        // when
        var newTaxStatus2 = new TaxStatusEntity(TEST);
        
        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> taxStatusRepo.save(newTaxStatus2)
        );
    }
}
