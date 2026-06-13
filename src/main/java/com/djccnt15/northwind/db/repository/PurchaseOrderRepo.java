package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.db.projection.PurchaseOrderTotalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrderEntity, Long> {

    @EntityGraph(attributePaths = {"status"})
    List<PurchaseOrderEntity> findByVendorIdOrderBySubmittedDateDesc(Long vendorId);

    @Query(value = """
        SELECT po FROM PurchaseOrderEntity po
        JOIN FETCH po.vendor v
        JOIN FETCH po.status s
        LEFT JOIN FETCH po.submittedBy sb
        WHERE v.name LIKE :kw
        AND (:statusId IS NULL OR s.id = :statusId)
        AND (:dateFrom IS NULL OR po.submittedDate >= :dateFrom)
        AND (:dateTo IS NULL OR po.submittedDate <= :dateTo)
        """,
        countQuery = """
        SELECT COUNT(po) FROM PurchaseOrderEntity po
        JOIN po.vendor v
        JOIN po.status s
        WHERE v.name LIKE :kw
        AND (:statusId IS NULL OR s.id = :statusId)
        AND (:dateFrom IS NULL OR po.submittedDate >= :dateFrom)
        AND (:dateTo IS NULL OR po.submittedDate <= :dateTo)
        """)
    Page<PurchaseOrderEntity> findByFilter(
        @Param("kw") String kw,
        @Param("statusId") Long statusId,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {
        "vendor", "status", "submittedBy", "approvedBy",
        "purchaseOrderDetails", "purchaseOrderDetails.product"
    })
    Optional<PurchaseOrderEntity> findWithDetailById(Long id);

    /**
     * Aggregate the item subtotal (unitPrice * quantity) per purchase order for a batch of ids,
     * plus the order's shippingFee, to compute totalAmount for the list view without fetching
     * the purchaseOrderDetails collection under pagination.
     */
    @Query("""
        SELECT po.id AS purchaseOrderId,
            COALESCE(SUM(d.unitPrice * d.quantity), 0)
                + COALESCE(po.shippingFee, 0) AS totalAmount
        FROM PurchaseOrderEntity po
        LEFT JOIN po.purchaseOrderDetails d
        WHERE po.id IN :ids
        GROUP BY po.id, po.shippingFee
        """)
    List<PurchaseOrderTotalProjection> findTotalAmountByIdIn(@Param("ids") List<Long> ids);
}
