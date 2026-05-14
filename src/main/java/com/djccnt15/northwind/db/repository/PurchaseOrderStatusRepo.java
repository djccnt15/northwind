package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderStatusRepo extends JpaRepository<PurchaseOrderStatusEntity, Long> {
}
