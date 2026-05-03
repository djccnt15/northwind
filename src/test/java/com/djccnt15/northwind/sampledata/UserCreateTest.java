package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")  // use application-dev.properties for testing
@DevTest
public class UserCreateTest {
    
    @Autowired private PasswordEncoder encoder;
    @Autowired private AppUserRepo userRepo;
    @Autowired private UserRoleRepo userRoleRepo;
    @Autowired private AppUserRoleRepo appUserRoleRepo;
    
    @Test
    void createUserRole() {
        var superAdmin = UserRoleEntity.builder()
            .name("SUPERADMIN")
            .build();
        var admin = UserRoleEntity.builder()
            .name("ADMIN")
            .build();
        var manager = UserRoleEntity.builder()
            .name("MANAGER")
            .build();
        var user = UserRoleEntity.builder()
            .name("USER")
            .build();
        userRoleRepo.saveAll(List.of(superAdmin, admin, manager, user));
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

        var userRole = userRoleRepo.findFirstByName("SUPERADMIN").orElseThrow();
        var appUserRole = AppUserRoleEntity.builder()
            .appUser(superAdmin)
            .userRole(userRole)
            .build();
        appUserRoleRepo.save(appUserRole);
    }

    @Test
    void createTestUsers() {
        var userRole = userRoleRepo.findFirstByName("USER").orElseThrow();
        for (int i = 0; i < 300; i++) {
            var user = AppUserEntity.builder()
                .username("user%s".formatted(i))
                .password(encoder.encode("user"))
                .email("user%s@user.com".formatted(i))
                .isVerified(true)
                .build();
            var appUserRole = AppUserRoleEntity.builder()
                .appUser(user)
                .userRole(userRole)
                .build();
            userRepo.save(user);
            appUserRoleRepo.save(appUserRole);
        }
    }
}
