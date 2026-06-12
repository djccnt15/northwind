package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusRepo extends JpaRepository<OrderStatusEntity, Long> {

    List<OrderStatusEntity> findAllByOrderByIdAsc();

    Optional<OrderStatusEntity> findFirstByOrderByIdAsc();

    Optional<OrderStatusEntity> findByCode(String code);
}
