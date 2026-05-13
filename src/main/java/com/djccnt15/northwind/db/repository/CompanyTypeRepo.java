package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyTypeRepo extends JpaRepository<CompanyTypeEntity, Long> {
}
