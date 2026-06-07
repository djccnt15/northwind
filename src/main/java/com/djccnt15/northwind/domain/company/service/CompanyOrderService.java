package com.djccnt15.northwind.domain.company.service;

import com.djccnt15.northwind.db.entity.OrdersEntity;
import com.djccnt15.northwind.db.repository.OrdersRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyOrderService {

    private final OrdersRepo repository;

    public List<OrdersEntity> getOrdersByCustomer(Long customerId) {
        return repository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }
}
