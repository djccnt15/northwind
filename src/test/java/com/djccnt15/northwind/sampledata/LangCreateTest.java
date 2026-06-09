package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.SupportedLangEntity;
import com.djccnt15.northwind.db.repository.SupportedLangRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class LangCreateTest {
    
    @Autowired private SupportedLangRepo langRepo;
    
    @Test
    void createLang() {
        var langList = List.of("en", "ko");
        var langEntityList = langList.stream()
            .map(SupportedLangEntity::new)
            .toList();
        
        langRepo.saveAll(langEntityList);
    }
}
