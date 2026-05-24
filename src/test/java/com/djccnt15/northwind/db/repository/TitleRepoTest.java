package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TitleEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static com.djccnt15.northwind.constants.TestConst.TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class TitleRepoTest {
    
    @Autowired private TitleRepo titleRepo;
    
    @Test
    void uniqueTitle() {
        // given
        var newTitle = new TitleEntity(TEST);
        titleRepo.save(newTitle);
        
        // when
        var newTitle2 = new TitleEntity(TEST);
        
        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> titleRepo.save(newTitle2)
        );
    }
}
