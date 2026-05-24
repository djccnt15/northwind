package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TeamEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static com.djccnt15.northwind.constants.TestConst.TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class TeamRepoTest {
    
    @Autowired private TeamRepo teamRepo;
    
    @Test
    void uniqueName() {
        // given
        var newTeam = new TeamEntity(TEST);
        teamRepo.save(newTeam);
        
        // when
        var newTeam2 = new TeamEntity(TEST);
        
        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> teamRepo.save(newTeam2)
        );
    }
}
