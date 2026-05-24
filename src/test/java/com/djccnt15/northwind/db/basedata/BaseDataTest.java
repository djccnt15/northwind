package com.djccnt15.northwind.db.basedata;

import com.djccnt15.northwind.db.entity.UserRoleEntity;
import com.djccnt15.northwind.db.repository.TeamRepo;
import com.djccnt15.northwind.db.repository.TitleRepo;
import com.djccnt15.northwind.db.repository.UserRoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static com.djccnt15.northwind.constants.TestConst.SYSTEM;
import static com.djccnt15.northwind.global.constants.RoleConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class BaseDataTest {
    
    @Autowired private UserRoleRepo roleRepo;
    @Autowired private TeamRepo teamRepo;
    @Autowired private TitleRepo titleRepo;
    
    private final String[] baseRoles = {SUPERADMIN, ADMIN, MANAGER, USER};
    
    @Test
    void testBaseRole() {
        var roleList = roleRepo.findAll();
        var roleNames = roleList.stream().map(UserRoleEntity::getName).toList();
        
        Arrays.stream(baseRoles).forEach(s ->
            assertThat(roleNames).contains(s)
        );
        
        roleList.forEach(r -> log.info("Base role: {}", r));
    }
    
    @Test
    void testSystemUser() {
        var systemUser = roleRepo.findById(1L).orElseThrow();
        assertThat(systemUser.getName()).isEqualTo(SUPERADMIN);
    }
    
    @Test
    void testSystemTeam() {
        var systemTeam = teamRepo.findById(1L).orElseThrow();
        assertThat(systemTeam.getName()).isEqualTo(SYSTEM);
    }
    
    @Test
    void testSystemTitle() {
        var systemTitle = titleRepo.findById(1L).orElseThrow();
        assertThat(systemTitle.getTitle()).isEqualTo(SYSTEM);
    }
}
