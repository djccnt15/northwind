package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TeamEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;

import static com.djccnt15.northwind.constants.TestConst.SYSTEM;
import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class TeamRepoTest {
    
    @Autowired private TeamRepo teamRepo;
    
    @Test
    void uniqueName() {
        var newName = getRandUuidString();
        
        // given
        var newTeam = new TeamEntity(newName);
        teamRepo.save(newTeam);
        
        // when
        var newTeam2 = new TeamEntity(newName);
        
        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> teamRepo.save(newTeam2)
        );
    }
    
    @Test
    void existsByName() {
        var exists = teamRepo.existsByName(SYSTEM);
        assertTrue(exists);
    }
    
    @Test
    void existsByNameAndIdNot() {
        var exists = teamRepo.existsByNameAndIdNot(SYSTEM, 1L);
        assertFalse(exists);
    }
    
    @Test
    void findFirstByName() {
        var systemTeam = teamRepo.findFirstByName(SYSTEM).orElseThrow();
        assertEquals(SYSTEM, systemTeam.getName());
    }
    
    @Test
    void findByNameLike() {
        var pageable = Pageable.unpaged();
        var kw = "%%%s%%".formatted("sys");
        var teams = teamRepo.findByNameLike(kw, pageable);
        assertFalse(teams.isEmpty());
    }
}
