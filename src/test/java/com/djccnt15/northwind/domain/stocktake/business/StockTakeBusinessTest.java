package com.djccnt15.northwind.domain.stocktake.business;

import com.djccnt15.northwind.domain.stocktake.model.StockTakeItemReq;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeSaveReq;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class StockTakeBusinessTest {

    // seeded product (P001 / Chai), discontinued = false
    private static final Long PRODUCT_ID = 1L;

    @Autowired private StockTakeBusiness stockTakeBusiness;

    private StockTakeRow firstRowOf(Long productId) {
        var rows = stockTakeBusiness.getStockTakeRows("", 0, 50).getContent();
        return rows.stream()
            .filter(r -> r.getProductId().equals(productId))
            .findFirst()
            .map(r -> new StockTakeRow(r.getExpectedQuantity(), r.getQuantityOnHand()))
            .orElseThrow();
    }

    private record StockTakeRow(Long expectedQuantity, Long quantityOnHand) {}

    @Test
    void getStockTakeRows_initiallyExpectedZeroAndNoDraft() {
        var row = firstRowOf(PRODUCT_ID);
        // no prior stock-take seeded -> expected 0, no draft today
        assertEquals(0L, row.expectedQuantity());
        assertNull(row.quantityOnHand());
    }

    @Test
    @Transactional
    void saveStockTakes_createsRecordAndDraftReflectedOnReload() {
        var today = LocalDate.now();
        var saved = stockTakeBusiness.saveStockTakes(new StockTakeSaveReq(
            today, List.of(new StockTakeItemReq(PRODUCT_ID, 35L))));

        assertEquals(1, saved.size());
        var savedRow = saved.get(0);
        assertEquals(PRODUCT_ID, savedRow.getProductId());
        assertEquals(0L, savedRow.getExpectedQuantity()); // no prior count -> baseline 0
        assertEquals(35L, savedRow.getQuantityOnHand());

        // reload: today's draft is reflected
        var reloaded = firstRowOf(PRODUCT_ID);
        assertEquals(35L, reloaded.quantityOnHand());
    }

    @Test
    @Transactional
    void saveStockTakes_sameDayUpsertKeepsSingleRecordAndUpdatesQuantity() {
        var today = LocalDate.now();
        stockTakeBusiness.saveStockTakes(new StockTakeSaveReq(
            today, List.of(new StockTakeItemReq(PRODUCT_ID, 35L))));
        var resaved = stockTakeBusiness.saveStockTakes(new StockTakeSaveReq(
            today, List.of(new StockTakeItemReq(PRODUCT_ID, 40L))));

        // upsert: quantity updated, expectedQuantity unchanged from create time (0)
        assertEquals(40L, resaved.get(0).getQuantityOnHand());
        assertEquals(0L, resaved.get(0).getExpectedQuantity());

        var reloaded = firstRowOf(PRODUCT_ID);
        assertEquals(40L, reloaded.quantityOnHand());
    }

    @Test
    @Transactional
    void saveStockTakes_expectedQuantityBecomesPreviousCountOnNextDate() {
        // count 35 yesterday, then count again today -> today's expected should be 35
        var yesterday = LocalDate.now().minusDays(1);
        stockTakeBusiness.saveStockTakes(new StockTakeSaveReq(
            yesterday, List.of(new StockTakeItemReq(PRODUCT_ID, 35L))));

        var today = LocalDate.now();
        var saved = stockTakeBusiness.saveStockTakes(new StockTakeSaveReq(
            today, List.of(new StockTakeItemReq(PRODUCT_ID, 30L))));

        assertEquals(35L, saved.get(0).getExpectedQuantity());
        assertEquals(30L, saved.get(0).getQuantityOnHand());
    }
}
