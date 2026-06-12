package com.djccnt15.northwind.global.schedule;

import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.repository.OrderDetailStatusRepo;
import com.djccnt15.northwind.db.repository.OrderStatusRepo;
import com.djccnt15.northwind.global.storage.DataCacheStorage;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RoleConst.SUPERADMIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {

    private final AppUserRepo appUserRepo;
    private final DataCacheStorage dataCacheStorage;
    private final OrderStatusRepo orderStatusRepo;
    private final OrderDetailStatusRepo orderDetailStatusRepo;

    /**
     * <p>This method will be called when the application context is refreshed (i.e., on startup)</p>
     * <p>You can perform any data warmup or initialization logic here</p>
     * <p>For example, you could load some initial data into the database, or perform some setup tasks</p>
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("Application context loaded. Performing data warmup...");
        var superAdmins = appUserRepo.findIdsByRoleName(SUPERADMIN);
        dataCacheStorage.updateData(superAdmins);
        seedOrderStatuses();
        seedOrderDetailStatuses();
    }

    /**
     * Idempotently seed the order_status master data (접수→결제완료→출고→배송완료→취소).
     */
    private void seedOrderStatuses() {
        if (orderStatusRepo.count() > 0) {
            return;
        }
        log.info("Seeding order_status master data...");
        orderStatusRepo.saveAll(List.of(
            OrderStatusEntity.builder().code("PENDING").name("접수").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("PAID").name("결제완료").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("SHIPPED").name("출고").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("DELIVERED").name("배송완료").sortOrder(SortOrderEnum.ASC).build(),
            OrderStatusEntity.builder().code("CANCELLED").name("취소").sortOrder(SortOrderEnum.ASC).build()
        ));
    }

    /**
     * Idempotently seed the order_detail_status master data (대기, 출고, 취소).
     */
    private void seedOrderDetailStatuses() {
        if (orderDetailStatusRepo.count() > 0) {
            return;
        }
        log.info("Seeding order_detail_status master data...");
        orderDetailStatusRepo.saveAll(List.of(
            OrderDetailStatusEntity.builder().name("대기").sortOrder(SortOrderEnum.ASC).build(),
            OrderDetailStatusEntity.builder().name("출고").sortOrder(SortOrderEnum.ASC).build(),
            OrderDetailStatusEntity.builder().name("취소").sortOrder(SortOrderEnum.ASC).build()
        ));
    }

    @Scheduled(cron = "${app.schedule.cron.superAdminRefresh:0 0 * * * *}") // default: every hour
    public void refreshSuperAdminCache() {
        log.info("Scheduled task: Refreshing super admin cache...");
        var superAdmins = appUserRepo.findIdsByRoleName(SUPERADMIN);
        dataCacheStorage.updateData(superAdmins);
    }
}
