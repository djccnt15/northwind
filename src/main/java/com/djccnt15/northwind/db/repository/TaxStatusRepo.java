package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxStatusRepo extends JpaRepository<TaxStatusEntity, Long> {
}
