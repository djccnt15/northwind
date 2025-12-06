package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepo extends JpaRepository<UserRoleEntity, Long> {
}
