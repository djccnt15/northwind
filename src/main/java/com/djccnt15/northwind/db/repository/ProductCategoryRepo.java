package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepo extends JpaRepository<ProductCategoryEntity, Long> {
}
