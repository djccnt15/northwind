package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrdersEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long> {

    @EntityGraph(attributePaths = {"orderStatus"})
    List<OrdersEntity> findByCustomerIdOrderByOrderDateDesc(Long customerId);
}
