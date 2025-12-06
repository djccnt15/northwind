package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepo extends JpaRepository<AppUserEntity, Long> {
}
