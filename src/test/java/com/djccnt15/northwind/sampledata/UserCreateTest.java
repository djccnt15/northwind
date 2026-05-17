package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.*;
import com.djccnt15.northwind.db.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.djccnt15.northwind.global.constants.RoleConst.*;

@SpringBootTest
@ActiveProfiles("dev")  // use application-dev.properties for testing
@DevTest
@Commit // commit the transaction to persist the data == @Rollback(false)
public class UserCreateTest {
    
    @Autowired private PasswordEncoder encoder;
    @Autowired private AppUserRepo userRepo;
    @Autowired private UserRoleRepo userRoleRepo;
    @Autowired private AppUserRoleRepo appUserRoleRepo;
    @Autowired private EmployeeRepo employeeRepo;
    @Autowired private TeamRepo teamRepo;
    @Autowired private TitleRepo titleRepo;
    
    @Test
    void createUserRole() {
        var superAdmin = new UserRoleEntity(SUPERADMIN);
        var admin = new UserRoleEntity(ADMIN);
        var manager = new UserRoleEntity(MANAGER);
        var user = new UserRoleEntity(USER);
        userRoleRepo.saveAll(List.of(superAdmin, admin, manager, user));
    }

    @Test
    @Transactional
    void createAdmin() {
        var teamEntity = teamRepo.findFirstByName("system").orElseThrow();
        var titleEntity = titleRepo.findFirstByTitle("system").orElseThrow();
        
        var superAdmin = AppUserEntity.builder()
            .username("admin")
            .password(encoder.encode("admin"))
            .email("admin@b.com")
            .isVerified(true)
            .team(teamEntity)
            .build();

        var userRole = userRoleRepo.findFirstByName("SUPERADMIN").orElseThrow();
        var appUserRole = AppUserRoleEntity.builder()
            .appUser(superAdmin)
            .userRole(userRole)
            .build();
        
        var employee = EmployeeEntity.builder()
            .firstName("admin")
            .lastName("admin")
            .hireDate(LocalDate.now())
            .appUser(superAdmin)
            .title(titleEntity)
            .build();
        
        userRepo.save(superAdmin);
        appUserRoleRepo.save(appUserRole);
        employeeRepo.save(employee);
    }
    
    @Test
    @Transactional
    void createTestUser() {
        var teamEntity = teamRepo.findFirstByName("system").orElseThrow();
        var titleEntity = titleRepo.findFirstByTitle("system").orElseThrow();
        var userRole = userRoleRepo.findFirstByName(USER).orElseThrow();
        
        var user = AppUserEntity.builder()
            .username("test")
            .password(encoder.encode("test"))
            .email("test@b.com")
            .isVerified(true)
            .team(teamEntity)
            .build();
        
        var appUserRole = AppUserRoleEntity.builder()
            .appUser(user)
            .userRole(userRole)
            .build();
        
        var employee = EmployeeEntity.builder()
            .firstName("test")
            .lastName("test")
            .hireDate(LocalDate.now())
            .appUser(user)
            .title(titleEntity)
            .build();
        
        userRepo.save(user);
        appUserRoleRepo.save(appUserRole);
        employeeRepo.save(employee);
    }

    @Test
    @Transactional
    void createTestUsers() {
        var userList = new ArrayList<AppUserEntity>();
        var appUserRoleList = new ArrayList<AppUserRoleEntity>();
        var employeeList = new ArrayList<EmployeeEntity>();
        
        var userRole = userRoleRepo.findFirstByName(USER).orElseThrow();
        var teamList = teamRepo.findAll();
        var titleList = titleRepo.findAll();
        
        for (int i = 0; i < 150; i++) {
            var random = new Random();
            var teamEntity = teamList.get(random.nextInt(5) + 1);
            
            var user = AppUserEntity.builder()
                .username("user%s".formatted(i))
                .password(encoder.encode("user"))
                .email("user%s@user.com".formatted(i))
                .isVerified(true)
                .team(teamEntity)
                .build();
            userList.add(user);
            
            var appUserRole = AppUserRoleEntity.builder()
                .appUser(user)
                .userRole(userRole)
                .build();
            appUserRoleList.add(appUserRole);
            
            TitleEntity titleEntity;
            if (i == 0) {
                titleEntity = titleList.stream().filter(it -> it.getTitle().equals("CEO")).findFirst().orElseThrow();
            } else if (i <= 10) {
                titleEntity = titleList.stream().filter(it -> it.getTitle().equals("DIRECTOR")).findFirst().orElseThrow();
            } else {
                titleEntity = titleList.get(random.nextInt(3) + 3);
            }
            
            var employee = EmployeeEntity.builder()
                .firstName("User")
                .lastName("%s".formatted(i))
                .hireDate(LocalDate.now())
                .appUser(user)
                .title(titleEntity)
                .build();
            employeeList.add(employee);
        }
        
        userRepo.saveAll(userList);
        appUserRoleRepo.saveAll(appUserRoleList);
        employeeRepo.saveAll(employeeList);
    }
}
