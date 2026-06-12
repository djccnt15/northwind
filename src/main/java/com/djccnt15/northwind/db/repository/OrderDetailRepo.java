package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDetailRepo extends JpaRepository<OrderDetailEntity, Long> {

    @EntityGraph(attributePaths = {"product", "orderDetailStatus", "order"})
    Optional<OrderDetailEntity> findWithRelationById(Long id);
}
