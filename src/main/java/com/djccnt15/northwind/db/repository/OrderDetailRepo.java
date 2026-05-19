package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepo extends JpaRepository<OrderDetailEntity, Long> {
}
