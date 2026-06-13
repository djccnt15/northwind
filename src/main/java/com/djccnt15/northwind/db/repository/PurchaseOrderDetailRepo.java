package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.PurchaseOrderDetailEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderDetailRepo extends JpaRepository<PurchaseOrderDetailEntity, Long> {

    @EntityGraph(attributePaths = {"product", "purchaseOrder"})
    Optional<PurchaseOrderDetailEntity> findWithRelationById(Long id);
}
