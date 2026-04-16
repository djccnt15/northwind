package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.AppUserRoleRepo;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
public class UserCreateTest {
    
    @Autowired private PasswordEncoder encoder;
    @Autowired private AppUserRepo userRepo;
    @Autowired private UserRoleRepo userRoleRepo;
    @Autowired private AppUserRoleRepo appUserRoleRepo;
    
    @Test
    void createUserRole() {
        var superAdmin = UserRoleEntity.builder()
            .name("superadmin")
            .build();
        var admin = UserRoleEntity.builder()
            .name("admin")
            .build();
        var user = UserRoleEntity.builder()
            .name("user")
            .build();
        userRoleRepo.saveAll(List.of(superAdmin, admin, user));
    }
    
    @Test
    void createAdmin() {
        var superAdmin = AppUserEntity.builder()
            .username("admin")
            .password(encoder.encode("admin"))
            .email("admin@b.com")
            .isVerified(true)
            .build();
        userRepo.save(superAdmin);
        
        var userRole = userRoleRepo.findFirstByName("superadmin").orElseThrow();
        var appUserRole = AppUserRoleEntity.builder()
            .appUser(superAdmin)
            .userRole(userRole)
            .build();
        appUserRoleRepo.save(appUserRole);
    }
}
