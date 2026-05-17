package com.djccnt15.northwind.global.schedule;

import com.djccnt15.northwind.global.storage.DataCacheStorage;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.djccnt15.northwind.global.constants.RoleConst.SUPERADMIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {
    
    private final AppUserRepo appUserRepo;
    private final DataCacheStorage dataCacheStorage;
    
    /**
     * <p>This method will be called when the application context is refreshed (i.e., on startup)</p>
     * <p>You can perform any data warmup or initialization logic here</p>
     * <p>For example, you could load some initial data into the database, or perform some setup tasks</p>
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        log.info("Application context refreshed. Performing data warmup...");
        var superAdmins = appUserRepo.findIdsByRoleName(SUPERADMIN);
        dataCacheStorage.updateData(superAdmins);
    }
    
    @Scheduled(cron = "${app.schedule.cron.superAdminRefresh:0 0 * * * *}") // default: every hour
    public void refreshSuperAdminCache() {
        log.info("Scheduled task: Refreshing super admin cache...");
        var superAdmins = appUserRepo.findIdsByRoleName(SUPERADMIN);
        dataCacheStorage.updateData(superAdmins);
    }
}
