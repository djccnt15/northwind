package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderDetailEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.CompanyRepo;
import com.djccnt15.northwind.db.repository.EmployeeRepo;
import com.djccnt15.northwind.db.repository.ProductRepo;
import com.djccnt15.northwind.db.repository.PurchaseOrderRepo;
import com.djccnt15.northwind.db.repository.PurchaseOrderStatusRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

@DevTest
@SpringBootTest
@Commit
public class PurchaseOrderCreateTest {

    @Autowired private PurchaseOrderRepo purchaseOrderRepo;
    @Autowired private AppUserRepo appUserRepo;
    @Autowired private EmployeeRepo employeeRepo;
    @Autowired private CompanyRepo companyRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private PurchaseOrderStatusRepo purchaseOrderStatusRepo;

    private static final int ORDERS_PER_STATUS = 3;

    // depends on: CompanyCreateTest, ProductCreateTest, PurchaseOrderStatusCreateTest, UserCreateTest(admin)
    @Test
    @Transactional
    void createPurchaseOrder() {
        var random = new Random();
        var adminUser = appUserRepo.findWithRoleFirstByUsername("admin").orElseThrow();
        var employee = employeeRepo.findFistByAppUser(adminUser).orElseThrow();
        var vendorList = companyRepo.findAll().stream()
            .filter(it -> it.getCompanyType().getCompanyType().equals("Supplier")).toList();
        var productList = productRepo.findAll();
        // DRAFT, PENDING_APPROVAL, APPROVED, RECEIVED, PAID, REJECTED
        var statusList = purchaseOrderStatusRepo.findAllByOrderByIdAsc();

        var purchaseOrderList = new ArrayList<PurchaseOrderEntity>();
        for (var s = 0; s < statusList.size(); s++) {
            var status = statusList.get(s);
            for (var n = 0; n < ORDERS_PER_STATUS; n++) {
                var vendor = vendorList.get(random.nextInt(vendorList.size()));
                var submittedDate = LocalDate.of(2026, 1, 1).plusDays(random.nextInt(120));

                var purchaseOrder = PurchaseOrderEntity.builder()
                    .submittedDate(submittedDate)
                    .shippingFee(random.nextInt(20) * 10)
                    .vendor(vendor)
                    .submittedBy(employee)
                    .status(status)
                    .build();

                applyStatusFields(purchaseOrder, status, employee, submittedDate);

                var detailCount = random.nextInt(3) + 1;
                for (var d = 0; d < detailCount; d++) {
                    var product = productList.get(random.nextInt(productList.size()));
                    purchaseOrder.getPurchaseOrderDetails().add(buildPurchaseOrderDetail(purchaseOrder, product, random));
                }

                purchaseOrderList.add(purchaseOrder);
            }
        }

        purchaseOrderRepo.saveAll(purchaseOrderList);
    }

    private void applyStatusFields(
        PurchaseOrderEntity purchaseOrder, PurchaseOrderStatusEntity status, EmployeeEntity employee,
        LocalDate submittedDate
    ) {
        switch (status.getCode()) {
            case "APPROVED" -> {
                purchaseOrder.setApprovedDate(submittedDate.plusDays(1));
                purchaseOrder.setApprovedBy(employee);
            }
            case "RECEIVED" -> {
                purchaseOrder.setApprovedDate(submittedDate.plusDays(1));
                purchaseOrder.setApprovedBy(employee);
                purchaseOrder.setReceivedDate(submittedDate.plusDays(5));
            }
            case "PAID" -> {
                purchaseOrder.setApprovedDate(submittedDate.plusDays(1));
                purchaseOrder.setApprovedBy(employee);
                purchaseOrder.setReceivedDate(submittedDate.plusDays(5));
                purchaseOrder.setPaymentDate(submittedDate.plusDays(10));
                purchaseOrder.setPaymentAmount(1000);
                purchaseOrder.setPaymentMethod("Bank Transfer");
            }
            default -> { /* DRAFT, PENDING_APPROVAL, REJECTED: only submittedDate is set */ }
        }
    }

    private PurchaseOrderDetailEntity buildPurchaseOrderDetail(
        PurchaseOrderEntity purchaseOrder, ProductEntity product, Random random
    ) {
        return PurchaseOrderDetailEntity.builder()
            .quantity((random.nextInt(10) + 1) * 5)
            .unitPrice(product.getStandardUnitCost())
            .product(product)
            .purchaseOrder(purchaseOrder)
            .build();
    }
}
