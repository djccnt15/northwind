package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.entity.StockTakeEntity;
import com.djccnt15.northwind.db.projection.StockTakeLatestProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockTakeRepo extends JpaRepository<StockTakeEntity, Long> {

    /**
     * Returns the latest (highest id) {@code quantity_on_hand} per product for the given product ids.
     * One query for the whole page (no N+1): the inner query groups by product to find the latest
     * stock-take id, the outer query reads its quantity.
     */
    @Query("""
        SELECT s.product.id AS productId, s.quantityOnHand AS quantityOnHand
        FROM StockTakeEntity s
        WHERE s.product.id IN :productIds
        AND s.id IN (
            SELECT MAX(s2.id) FROM StockTakeEntity s2
            WHERE s2.product.id IN :productIds
            GROUP BY s2.product.id
        )
        """)
    List<StockTakeLatestProjection> findLatestByProductIds(@Param("productIds") List<Long> productIds);

    /**
     * Today's draft stock-take records for the given products (used to pre-fill the grid).
     */
    @Query("""
        SELECT s.product.id AS productId, s.quantityOnHand AS quantityOnHand
        FROM StockTakeEntity s
        WHERE s.product.id IN :productIds
        AND s.stockTakeDate = :date
        """)
    List<StockTakeLatestProjection> findDraftByProductIdsAndStockTakeDate(
        @Param("productIds") List<Long> productIds,
        @Param("date") LocalDate date
    );

    /**
     * Existing stock-take record for (product, date) used to decide upsert (update vs insert).
     */
    Optional<StockTakeEntity> findByProductAndStockTakeDate(ProductEntity product, LocalDate stockTakeDate);

    /**
     * The single latest stock-take quantity for a product, used as the "expected quantity"
     * baseline when creating a new record. Empty when no prior stock-take exists.
     */
    Optional<StockTakeEntity> findFirstByProductOrderByIdDesc(ProductEntity product);
}
