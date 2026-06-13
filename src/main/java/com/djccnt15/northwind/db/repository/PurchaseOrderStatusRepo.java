package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderStatusRepo extends JpaRepository<PurchaseOrderStatusEntity, Long> {

    List<PurchaseOrderStatusEntity> findAllByOrderByIdAsc();

    Optional<PurchaseOrderStatusEntity> findFirstByOrderByIdAsc();

    Optional<PurchaseOrderStatusEntity> findByCode(String code);
}
