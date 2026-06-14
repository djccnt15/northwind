package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.repository.OrderStatusRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class OrderStatusCreateTest {

    @Autowired private OrderStatusRepo orderStatusRepo;

    @Test
    void createOrderStatus() {
        // codes must match OrderService.CODE_* constants (id order = sort order: 접수->결제완료->출고->배송완료->취소)
        var statusList = List.of(
            OrderStatusEntity.builder().code("PENDING").name("pending").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("PAID").name("paid").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("SHIPPED").name("shipped").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("DELIVERED").name("delivered").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("CANCELLED").name("cancelled").sortOrder(SortOrderEnum.ASC).build()
        );

        orderStatusRepo.saveAll(statusList);
    }
}
