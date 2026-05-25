package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<EmployeeEntity, Long> {
    
    @EntityGraph(attributePaths = {"title"})
    Optional<EmployeeEntity> findFistByAppUser(AppUserEntity appUser);
}
