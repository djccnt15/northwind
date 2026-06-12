package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDetailStatusRepo extends JpaRepository<OrderDetailStatusEntity, Long> {

    List<OrderDetailStatusEntity> findAllByOrderByIdAsc();

    Optional<OrderDetailStatusEntity> findFirstByOrderByIdAsc();
}
