package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<EmployeeEntity, Long> {
}
