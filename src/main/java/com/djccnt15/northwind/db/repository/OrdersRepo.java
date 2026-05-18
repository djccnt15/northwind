package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long> {
}
