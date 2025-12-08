package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.entity.id.AppUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRoleRepo extends JpaRepository<AppUserRoleEntity, AppUserRoleId> {
}
