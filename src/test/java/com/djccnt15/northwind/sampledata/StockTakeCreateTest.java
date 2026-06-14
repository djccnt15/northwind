package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.StockTakeEntity;
import com.djccnt15.northwind.db.repository.ProductRepo;
import com.djccnt15.northwind.db.repository.StockTakeRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DevTest
@SpringBootTest
@Commit
public class StockTakeCreateTest {

    @Autowired private StockTakeRepo stockTakeRepo;
    @Autowired private ProductRepo productRepo;

    private static final List<LocalDate> STOCK_TAKE_DATES = List.of(
        LocalDate.of(2026, 4, 1),
        LocalDate.of(2026, 5, 1),
        LocalDate.of(2026, 6, 1)
    );

    // depends on: ProductCreateTest
    @Test
    @Transactional
    void createStockTake() {
        var random = new Random();
        var productList = productRepo.findAll();

        var stockTakeList = new ArrayList<StockTakeEntity>();
        for (var product : productList) {
            var expectedQuantity = (long) product.getTargetLevel();
            for (var date : STOCK_TAKE_DATES) {
                var quantityOnHand = Math.max(0, expectedQuantity + random.nextInt(11) - 5);

                stockTakeList.add(StockTakeEntity.builder()
                    .stockTakeDate(date)
                    .quantityOnHand(quantityOnHand)
                    .expectedQuantity(expectedQuantity)
                    .product(product)
                    .build());

                expectedQuantity = quantityOnHand;
            }
        }

        stockTakeRepo.saveAll(stockTakeList);
    }
}
