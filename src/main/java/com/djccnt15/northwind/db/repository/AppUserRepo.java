package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUserEntity, Long> {
    
    @EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole"})
    Optional<AppUserEntity> findWithRoleFirstByUsername(String username);
    
    Optional<AppUserEntity> findFirstByUsername(String username);
    
    Optional<AppUserEntity> findFirstByUsernameAndIdNot(String username, Long id);
    
    Optional<AppUserEntity> findFirstByEmail(String email);
    
    Optional<AppUserEntity> findFirstByEmailAndIdNot(String email, Long id);
}
