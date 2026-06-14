package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.repository.PurchaseOrderStatusRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class PurchaseOrderStatusCreateTest {

    @Autowired private PurchaseOrderStatusRepo purchaseOrderStatusRepo;

    @Test
    void createPurchaseOrderStatus() {
        // codes must match PurchaseOrderService.CODE_* constants (id order = sort order: DRAFT..PAID, then REJECTED)
        var statusList = List.of(
            PurchaseOrderStatusEntity.builder().code("DRAFT").name("draft").sortOrder(SortOrderEnum.ASC).build(),
            PurchaseOrderStatusEntity.builder().code("PENDING_APPROVAL").name("pending_approval").sortOrder(SortOrderEnum.ASC).build(),
            PurchaseOrderStatusEntity.builder().code("APPROVED").name("approved").sortOrder(SortOrderEnum.ASC).build(),
            PurchaseOrderStatusEntity.builder().code("RECEIVED").name("received").sortOrder(SortOrderEnum.ASC).build(),
            PurchaseOrderStatusEntity.builder().code("PAID").name("paid").sortOrder(SortOrderEnum.ASC).build(),
            PurchaseOrderStatusEntity.builder().code("REJECTED").name("rejected").sortOrder(SortOrderEnum.ASC).build()
        );

        purchaseOrderStatusRepo.saveAll(statusList);
    }
}
