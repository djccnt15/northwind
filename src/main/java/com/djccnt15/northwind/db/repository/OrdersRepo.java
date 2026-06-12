package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.db.projection.OrderTotalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long> {

    @EntityGraph(attributePaths = {"orderStatus"})
    List<OrdersEntity> findByCustomerIdOrderByOrderDateDesc(Long customerId);

    @Query(value = """
        SELECT o FROM OrdersEntity o
        JOIN FETCH o.customer c
        JOIN FETCH o.orderStatus s
        LEFT JOIN FETCH o.shipper sh
        WHERE c.name LIKE :kw
        AND (:statusId IS NULL OR s.id = :statusId)
        AND (:dateFrom IS NULL OR o.orderDate >= :dateFrom)
        AND (:dateTo IS NULL OR o.orderDate <= :dateTo)
        """,
        countQuery = """
        SELECT COUNT(o) FROM OrdersEntity o
        JOIN o.customer c
        JOIN o.orderStatus s
        WHERE c.name LIKE :kw
        AND (:statusId IS NULL OR s.id = :statusId)
        AND (:dateFrom IS NULL OR o.orderDate >= :dateFrom)
        AND (:dateTo IS NULL OR o.orderDate <= :dateTo)
        """)
    Page<OrdersEntity> findByFilter(
        @Param("kw") String kw,
        @Param("statusId") Long statusId,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {
        "customer", "shipper", "taxStatus", "orderStatus", "appUser",
        "orderDetails", "orderDetails.product", "orderDetails.orderDetailStatus"
    })
    Optional<OrdersEntity> findWithDetailById(Long id);

    /**
     * Aggregate the item subtotal (unitPrice * quantity * (1 - discount/100)) per order
     * for a batch of order ids, plus the order's shippingFee, to compute totalAmount
     * for the list view without fetching the orderDetails collection under pagination.
     */
    @Query("""
        SELECT o.id AS orderId,
            COALESCE(SUM(d.unitPrice * d.quantity * (100 - COALESCE(d.discount, 0)) / 100), 0)
                + COALESCE(o.shippingFee, 0) AS totalAmount
        FROM OrdersEntity o
        LEFT JOIN o.orderDetails d
        WHERE o.id IN :ids
        GROUP BY o.id, o.shippingFee
        """)
    List<OrderTotalProjection> findTotalAmountByIdIn(@Param("ids") List<Long> ids);
}
