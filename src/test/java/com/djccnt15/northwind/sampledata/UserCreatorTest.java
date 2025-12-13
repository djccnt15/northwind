package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserCreatorTest {
    
    @Autowired private PasswordEncoder encoder;
    @Autowired private AppUserRepo repository;
    
    @Test
    void createAdmin() {
        var admin = AppUserEntity.builder()
            .username("admin")
            .password(encoder.encode("admin"))
            .email("admin@b.com")
            .build();
    }
}
