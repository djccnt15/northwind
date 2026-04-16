package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepo extends JpaRepository<UserRoleEntity, Long> {
    
    Optional<UserRoleEntity> findFirstByName(String name);
}
