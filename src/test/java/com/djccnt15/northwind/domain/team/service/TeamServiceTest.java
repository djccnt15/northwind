package com.djccnt15.northwind.domain.team.service;

import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.TeamRepo;
import com.djccnt15.northwind.domain.team.model.TeamCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static com.djccnt15.northwind.constants.TestConst.SYSTEM;
import static com.djccnt15.northwind.constants.TestConst.TEST;
import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TeamServiceTest {
    
    @Autowired private TeamService service;
    @Autowired private AppUserRepo userRepo;
    
    @Test
    void validateTeam() {
        // given
        var req1 = new TeamCreateReq(SYSTEM);
        var req2 = new TeamCreateReq(TEST);
        
        // when & then
        assertThrows(
            ApiException.class,
            () -> service.validateTeam(req1)
        );
        assertDoesNotThrow(() -> service.validateTeam(req2));
        
        // given
        var ogReq = new TeamCreateReq(getRandUuidString());
        var ogTeam = service.createTeam(ogReq);
        var newReq = new TeamCreateReq(getRandUuidString());
        service.createTeam(newReq);
        
        // when & then
        assertThrows(
            ApiException.class,
            () -> service.validateTeam(ogTeam.getId(), newReq)
        );
        assertDoesNotThrow(() -> service.validateTeam(ogTeam.getId(), ogReq));
    }
    
    @Test
    void createTeam() {
        // given
        var req = new TeamCreateReq(getRandUuidString());
        
        // when
        var team = service.createTeam(req);
        
        // then
        assertNotNull(team);
        assertEquals(req.getName(), team.getName());
    }
    
    @Test
    void getTeams() {
        // given
        var kw = "%%%s%%".formatted("sys");
        var pageable = Pageable.unpaged();
        
        // when
        var page = service.getTeams(kw, pageable);
        
        // then
        assertNotNull(page);
        assertTrue(page.getTotalElements() > 0);
        
        // when
        var teams = service.getTeams();
        
        // then
        assertNotNull(teams);
        assertFalse(teams.isEmpty());
    }
    
    @Test
    void getTeam() {
        // given
        var id = 1L;
        var name = SYSTEM;
        
        // when
        var teamById = service.getTeam(id);
        var teamByName = service.getTeam(name);
        
        // then
        assertNotNull(teamById);
        assertEquals(id, teamById.getId());
        assertNotNull(teamByName);
        assertEquals(name, teamByName.getName());
        
        // when & then
        assertThrows(
            ApiException.class,
            () -> service.getTeam(-1L)
        );
        assertThrows(
            ApiException.class,
            () -> service.getTeam(getRandUuidString())
        );
    }
    
    @Test
    void updateTeam() {
        // given
        var request = new TeamCreateReq(getRandUuidString());
        var entity = service.createTeam(request);
        
        var updateName = getRandUuidString();
        var updateReq = new TeamCreateReq(updateName);
        
        // when
        service.updateTeam(entity, updateReq);
        
        // then
        var updated = service.getTeam(entity.getId());
        assertNotNull(updated);
        assertEquals(updateName, updated.getName());
    }
    
    @Test
    void deleteTeam() {
        // given
        var request = new TeamCreateReq(getRandUuidString());
        var entity = service.createTeam(request);
        
        // when
        service.deleteTeam(entity);
        
        // then
        assertThrows(
            ApiException.class,
            () -> service.getTeam(entity.getId())
        );
    }
    
    @Test
    @Transactional
    void addMember() {
        // given
        var team = service.getTeam(1L);
        var user = userRepo.findById(1L).orElseThrow();
        
        // when
        service.addMember(team, user);
        
        // then
        var updated = service.getTeam(team.getId());
        assertNotNull(updated);
        assertTrue(updated.getMembers().contains(user));
    }
    
    @Test
    @Transactional
    void removeMember() {
        // given
        var team = service.getTeam(1L);
        var user = userRepo.findById(1L).orElseThrow();
        
        // when
        service.removeMember(team, user);
        
        // then
        var updated = service.getTeam(team.getId());
        assertNotNull(updated);
        assertFalse(updated.getMembers().contains(user));
    }
}
