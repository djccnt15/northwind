package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<CompanyEntity, Long> {
}
