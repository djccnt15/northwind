package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.TeamEntity;
import com.djccnt15.northwind.db.repository.TeamRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootTest
@ActiveProfiles("dev")  // use application-dev.properties for testing
@DevTest
@Commit
public class TeamCreateTest {
    
    @Autowired private TeamRepo teamRepo;
    
    @Test
    void createTeam() {
        var teamList = new ArrayList<TeamEntity>();
        
        var adminTeam = new TeamEntity("system", new HashSet<>());
        teamList.add(adminTeam);
        
        for (int i = 0; i < 5; i++) {
            var team = new TeamEntity("Team %d".formatted(i + 1), new HashSet<>());
            teamList.add(team);
        }
        
        teamRepo.saveAll(teamList);
    }
}
