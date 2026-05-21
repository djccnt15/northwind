package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.MruEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MruRepo extends JpaRepository<MruEntity, Long> {
}
