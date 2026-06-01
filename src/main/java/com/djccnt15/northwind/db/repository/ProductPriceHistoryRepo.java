package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductPriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceHistoryRepo extends JpaRepository<ProductPriceHistoryEntity, Long> {
}
