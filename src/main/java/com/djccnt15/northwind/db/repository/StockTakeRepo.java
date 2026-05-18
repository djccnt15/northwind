package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.StockTakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTakeRepo extends JpaRepository<StockTakeEntity, Long> {
}
