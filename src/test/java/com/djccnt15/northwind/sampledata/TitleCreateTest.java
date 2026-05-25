package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.TitleEntity;
import com.djccnt15.northwind.db.repository.TitleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@DevTest
@SpringBootTest
@ActiveProfiles("dev")  // use application-dev.properties for testing
@Commit
public class TitleCreateTest {
    
    @Autowired private TitleRepo titleRepo;
    
    @Test
    void createTitles() {
        var titles = List.of("system", "CEO", "DIRECTOR", "MANAGER", "STAFF", "INTERN");
        var titleList = new ArrayList<TitleEntity>();
        
        titles.stream()
            .map(TitleEntity::new)
            .forEach(titleList::add)
        ;
        
        titleRepo.saveAll(titleList);
    }
}
