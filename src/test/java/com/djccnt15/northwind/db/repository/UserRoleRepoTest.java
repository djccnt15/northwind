package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.UserRoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static com.djccnt15.northwind.constants.TestConst.TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
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
}
