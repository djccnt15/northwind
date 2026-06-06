package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.UserRoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static com.djccnt15.northwind.constants.TestConst.TEST;
import static com.djccnt15.northwind.global.constants.RoleConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserRoleRepoTest {
    
    @Autowired private UserRoleRepo roleRepo;
    
    @Test
    void uniqueName() {
        // given
        var newRole = new UserRoleEntity(TEST);
        roleRepo.save(newRole);
        
        // when
        var newRole2 = new UserRoleEntity(TEST);
        
        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> roleRepo.save(newRole2)
        );
    }
    
    @Test
    void findFirstByName() {
        var systemRole = roleRepo.findFirstByName(SUPERADMIN).orElseThrow();
        assertEquals(SUPERADMIN, systemRole.getName());
    }
    
    @Test
    void findAllByNameInOrderByName() {
        var roleList = List.of(SUPERADMIN, USER, ADMIN);
        var roles = roleRepo.findAllByNameInOrderByName(roleList);
        
        assertEquals(3, roles.size());
        assertEquals(ADMIN, roles.get(0).getName());
        assertEquals(SUPERADMIN, roles.get(1).getName());
        assertEquals(USER, roles.get(2).getName());
    }
}
