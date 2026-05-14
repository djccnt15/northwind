package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepo extends JpaRepository<OrderStatusEntity, Long> {
}
