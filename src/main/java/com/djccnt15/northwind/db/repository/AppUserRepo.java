package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUserEntity, Long> {
    
    Optional<AppUserEntity> findFirstByUsername(String username);
}
