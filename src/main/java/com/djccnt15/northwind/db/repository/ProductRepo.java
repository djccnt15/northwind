package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
}
