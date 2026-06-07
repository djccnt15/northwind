package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrderEntity, Long> {

    @EntityGraph(attributePaths = {"status"})
    List<PurchaseOrderEntity> findByVendorIdOrderBySubmittedDateDesc(Long vendorId);
}
