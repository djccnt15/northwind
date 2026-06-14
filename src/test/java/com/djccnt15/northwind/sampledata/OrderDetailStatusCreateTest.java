package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.repository.OrderDetailStatusRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class OrderDetailStatusCreateTest {

    @Autowired private OrderDetailStatusRepo orderDetailStatusRepo;

    @Test
    void createOrderDetailStatus() {
        // id order = sort order: 대기->출고->취소
        var statusList = List.of(
            OrderDetailStatusEntity.builder().name("pending").sortOrder(SortOrderEnum.ASC).build(),
            OrderDetailStatusEntity.builder().name("shipped").sortOrder(SortOrderEnum.ASC).build(),
            OrderDetailStatusEntity.builder().name("cancelled").sortOrder(SortOrderEnum.ASC).build()
        );

        orderDetailStatusRepo.saveAll(statusList);
    }
}
