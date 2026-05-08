package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.TitleEntity;
import com.djccnt15.northwind.db.repository.TitleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

@SpringBootTest
@ActiveProfiles("dev")  // use application-dev.properties for testing
@DevTest
public class TitleCreateTest {
    
    @Autowired private TitleRepo titleRepo;
    
    @Test
    void createTitles() {
        var titleList = new ArrayList<TitleEntity>();
        for (int i = 0; i < 10; i++) {
            var title = TitleEntity.builder()
                .title("title%s".formatted(i))
                .build();
            titleList.add(title);
        }
        titleRepo.saveAll(titleList);
    }
}
