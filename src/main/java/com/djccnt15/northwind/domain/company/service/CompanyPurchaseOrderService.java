package com.djccnt15.northwind.domain.company.service;

import com.djccnt15.northwind.db.entity.PurchaseOrderEntity;
import com.djccnt15.northwind.db.repository.PurchaseOrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyPurchaseOrderService {

    private final PurchaseOrderRepo repository;

    public List<PurchaseOrderEntity> getPurchaseOrdersByVendor(Long vendorId) {
        return repository.findByVendorIdOrderBySubmittedDateDesc(vendorId);
    }
}
