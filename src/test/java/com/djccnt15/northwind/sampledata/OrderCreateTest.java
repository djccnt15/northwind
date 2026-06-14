package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.OrderDetailEntity;
import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.db.repository.CompanyRepo;
import com.djccnt15.northwind.db.repository.OrderDetailStatusRepo;
import com.djccnt15.northwind.db.repository.OrderStatusRepo;
import com.djccnt15.northwind.db.repository.OrdersRepo;
import com.djccnt15.northwind.db.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DevTest
@SpringBootTest
@Commit
public class OrderCreateTest {

    @Autowired private OrdersRepo ordersRepo;
    @Autowired private AppUserRepo appUserRepo;
    @Autowired private CompanyRepo companyRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private OrderStatusRepo orderStatusRepo;
    @Autowired private OrderDetailStatusRepo orderDetailStatusRepo;

    private static final int ORDER_COUNT = 30;

    // depends on: CompanyCreateTest, ProductCreateTest, OrderStatusCreateTest, OrderDetailStatusCreateTest
    @Test
    @Transactional
    void createOrder() {
        var random = new Random();
        var appUser = appUserRepo.findWithRoleFirstByUsername("admin").orElseThrow();
        var customerList = companyRepo.findAll().stream()
            .filter(it -> it.getCompanyType().getCompanyType().equals("Customer"))            .toList();
        var shipperList = companyRepo.findAll().stream()
            .filter(it -> it.getCompanyType().getCompanyType().equals("Supplier"))            .toList();
        var productList = productRepo.findAll();
        var orderStatusList = orderStatusRepo.findAllByOrderByIdAsc(); // PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
        var detailStatusList = orderDetailStatusRepo.findAllByOrderByIdAsc(); // 대기, 출고, 취소

        var pending = orderStatusList.get(0);
        var paid = orderStatusList.get(1);
        var shipped = orderStatusList.get(2);
        var delivered = orderStatusList.get(3);
        var cancelled = orderStatusList.get(4);
        var waiting = detailStatusList.get(0);
        var shippedOut = detailStatusList.get(1);
        var canceled = detailStatusList.get(2);

        var orderList = new ArrayList<OrdersEntity>();
        for (var i = 0; i < ORDER_COUNT; i++) {
            var customer = customerList.get(random.nextInt(customerList.size()));
            var shipper = shipperList.get(random.nextInt(shipperList.size()));
            var orderDate = LocalDate.of(2026, 1, 1).plusDays(random.nextInt(120));

            OrderStatusEntity orderStatus;
            OrderDetailStatusEntity detailStatus;
            LocalDate paidDate = null;
            LocalDate shippedDate = null;
            switch (i % 5) {
                case 0 -> {
                    orderStatus = pending;
                    detailStatus = waiting;
                }
                case 1 -> {
                    orderStatus = paid;
                    detailStatus = waiting;
                    paidDate = orderDate.plusDays(1);
                }
                case 2 -> {
                    orderStatus = shipped;
                    detailStatus = shippedOut;
                    paidDate = orderDate.plusDays(1);
                    shippedDate = orderDate.plusDays(3);
                }
                case 3 -> {
                    orderStatus = delivered;
                    detailStatus = shippedOut;
                    paidDate = orderDate.plusDays(1);
                    shippedDate = orderDate.plusDays(3);
                }
                default -> {
                    orderStatus = cancelled;
                    detailStatus = canceled;
                }
            }

            var order = OrdersEntity.builder()
                .orderDate(orderDate)
                .shippingFee(random.nextInt(20) * 10)
                .paymentType("Credit Card")
                .paidDate(paidDate)
                .shippedDate(shippedDate)
                .appUser(appUser)
                .customer(customer)
                .shipper(shipper)
                .taxStatus(customer.getTaxStatus())
                .orderStatus(orderStatus)
                .build();

            var detailCount = random.nextInt(3) + 1;
            for (var d = 0; d < detailCount; d++) {
                var product = productList.get(random.nextInt(productList.size()));
                order.getOrderDetails().add(buildOrderDetail(order, product, detailStatus, random));
            }

            orderList.add(order);
        }

        ordersRepo.saveAll(orderList);
    }

    private OrderDetailEntity buildOrderDetail(
        OrdersEntity order, ProductEntity product, OrderDetailStatusEntity detailStatus, Random random
    ) {
        return OrderDetailEntity.builder()
            .quantity(random.nextInt(10) + 1)
            .unitPrice(product.getUnitPrice())
            .standardUnitCost(product.getStandardUnitCost())
            .discount(List.of(0, 0, 0, 5, 10).get(random.nextInt(5)))
            .product(product)
            .order(order)
            .orderDetailStatus(detailStatus)
            .build();
    }
}
