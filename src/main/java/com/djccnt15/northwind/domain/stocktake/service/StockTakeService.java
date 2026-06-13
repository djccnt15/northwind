package com.djccnt15.northwind.domain.stocktake.service;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.entity.StockTakeEntity;
import com.djccnt15.northwind.db.projection.StockTakeLatestProjection;
import com.djccnt15.northwind.db.repository.StockTakeRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.djccnt15.northwind.domain.stocktake.validation.StockTakeErrorConst.CONFLICT_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTakeService {

    private final StockTakeRepo repository;
    private final MessageUtil messageUtil;

    /**
     * Latest quantity-on-hand per product id (the "computer stock"). Products absent from the map
     * have never been counted and default to 0 at the call site.
     */
    public Map<Long, Long> getLatestQuantities(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return Map.of();
        }
        return repository.findLatestByProductIds(productIds).stream()
            .collect(Collectors.toMap(
                StockTakeLatestProjection::getProductId,
                StockTakeLatestProjection::getQuantityOnHand
            ));
    }

    /**
     * Today's draft quantity-on-hand per product id (records already saved for the given date).
     */
    public Map<Long, Long> getDraftQuantities(List<Long> productIds, LocalDate date) {
        if (productIds.isEmpty()) {
            return Map.of();
        }
        return repository.findDraftByProductIdsAndStockTakeDate(productIds, date).stream()
            .collect(Collectors.toMap(
                StockTakeLatestProjection::getProductId,
                StockTakeLatestProjection::getQuantityOnHand
            ));
    }

    /**
     * Upsert a single stock-take record for (product, date):
     * - existing record for the date -> update quantityOnHand (expectedQuantity stays fixed)
     * - no record -> create one, fixing expectedQuantity to the latest prior quantityOnHand (0 when none)
     * Optimistic-lock conflicts are translated to a BAD_REQUEST ApiException.
     */
    public StockTakeEntity upsert(ProductEntity product, LocalDate date, Long quantityOnHand) {
        try {
            var existing = repository.findByProductAndStockTakeDate(product, date).orElse(null);
            if (existing != null) {
                existing.setQuantityOnHand(quantityOnHand);
                return repository.saveAndFlush(existing);
            }
            var expected = repository.findFirstByProductOrderByIdDesc(product)
                .map(StockTakeEntity::getQuantityOnHand)
                .orElse(0L);
            var entity = StockTakeEntity.builder()
                .product(product)
                .stockTakeDate(date)
                .quantityOnHand(quantityOnHand)
                .expectedQuantity(expected)
                .build();
            return repository.saveAndFlush(entity);
        } catch (OptimisticLockingFailureException e) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(CONFLICT_ERR_MSG));
        }
    }
}
