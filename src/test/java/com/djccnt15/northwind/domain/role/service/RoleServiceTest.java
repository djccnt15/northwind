package com.djccnt15.northwind.domain.role.service;

import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RoleConst.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RoleServiceTest {
    
    @Autowired private RoleService roleService;
    
    @Test
    void getRoles() {
        var roleList = List.of(SUPERADMIN, USER, ADMIN);
        var roles = roleService.getRoles(roleList);
        
        assertEquals(roleList.size(), roles.size());
        assertEquals(ADMIN, roles.get(0).getName());
        assertEquals(SUPERADMIN, roles.get(1).getName());
        assertEquals(USER, roles.get(2).getName());
    }
    
    @Test
    void getAllRoles() {
        var roles = roleService.getAllRoles();
        
        assertFalse(roles.contains(SUPERADMIN));
        assertTrue(roles.contains(ADMIN));
        assertTrue(roles.contains(USER));
        assertNotNull(roles);
    }
    
    @Test
    void validateNotSuperAdmin() {
        // given
        var superAdminId = 1L;
        var nonSuperAdminId = 2L;
        
        // when & then
        assertThrows(
            ApiException.class,
            () -> roleService.validateNotSuperAdmin(superAdminId)
        );
        
        assertDoesNotThrow(
            () -> roleService.validateNotSuperAdmin(nonSuperAdminId)
        );
    }
}
