package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductVendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVendorRepo extends JpaRepository<ProductVendorEntity, Long> {
}
