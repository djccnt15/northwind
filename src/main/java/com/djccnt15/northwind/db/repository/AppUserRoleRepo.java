package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.AppUserRoleEntity;
import com.djccnt15.northwind.db.entity.id.AppUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface AppUserRoleRepo extends JpaRepository<AppUserRoleEntity, AppUserRoleId> {
    
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteAllByAppUser(AppUserEntity userEntity);
}
