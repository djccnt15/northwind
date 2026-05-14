package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailStatusRepo extends JpaRepository<OrderDetailStatusEntity, Long> {
}
