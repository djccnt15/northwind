package com.djccnt15.northwind.domain.stocktake.service;

import com.djccnt15.northwind.db.entity.StockTakeEntity;
import com.djccnt15.northwind.db.repository.ProductRepo;
import com.djccnt15.northwind.db.repository.StockTakeRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class StockTakeServiceTest {

    private static final Long PRODUCT_ID = 1L;

    @Autowired private StockTakeService stockTakeService;
    @Autowired private StockTakeRepo stockTakeRepo;
    @Autowired private ProductRepo productRepo;

    @Test
    @Transactional
    void upsert_createsWithExpectedZeroWhenNoPrior() {
        var product = productRepo.findById(PRODUCT_ID).orElseThrow();
        var saved = stockTakeService.upsert(product, LocalDate.now(), 25L);

        assertNotNull(saved.getId());
        assertEquals(0L, saved.getExpectedQuantity());
        assertEquals(25L, saved.getQuantityOnHand());
        assertNotNull(saved.getVersion());
    }

    @Test
    @Transactional
    void upsert_sameDayUpdatesQuantityKeepingExpected() {
        var product = productRepo.findById(PRODUCT_ID).orElseThrow();
        stockTakeService.upsert(product, LocalDate.now(), 25L);
        var updated = stockTakeService.upsert(product, LocalDate.now(), 31L);

        assertEquals(31L, updated.getQuantityOnHand());
        assertEquals(0L, updated.getExpectedQuantity());
        // one record kept for (product, date)
        assertTrue(stockTakeRepo.findByProductAndStockTakeDate(product, LocalDate.now()).isPresent());
    }

    /**
     * The @Version column must enforce optimistic locking at the persistence layer: saving a
     * detached copy carrying an outdated version against an already-advanced row must fail. The
     * service's upsert() catches this same exception and rethrows it as a BAD_REQUEST ApiException.
     */
    @Test
    @Transactional
    void version_enforcesOptimisticLocking() {
        var product = productRepo.findById(PRODUCT_ID).orElseThrow();
        var saved = stockTakeService.upsert(product, LocalDate.now(), 25L);
        var originalVersion = saved.getVersion();

        // advance the persistent row's version
        var fresh = stockTakeRepo.findById(saved.getId()).orElseThrow();
        fresh.setQuantityOnHand(30L);
        stockTakeRepo.saveAndFlush(fresh);

        // a detached copy still on the original version must conflict
        var stale = StockTakeEntity.builder()
            .product(product)
            .stockTakeDate(saved.getStockTakeDate())
            .quantityOnHand(99L)
            .expectedQuantity(saved.getExpectedQuantity())
            .build();
        stale.setId(saved.getId());
        stale.setVersion(originalVersion);

        assertThrows(OptimisticLockingFailureException.class, () -> stockTakeRepo.saveAndFlush(stale));
    }
}
