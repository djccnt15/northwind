package com.djccnt15.northwind.domain.purchase.service;

import com.djccnt15.northwind.db.entity.PurchaseOrderStatusEntity;
import com.djccnt15.northwind.db.repository.PurchaseOrderStatusRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderErrorConst.STATUS_NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderStatusService {

    private final PurchaseOrderStatusRepo repository;
    private final MessageUtil messageUtil;

    public List<PurchaseOrderStatusEntity> getPurchaseOrderStatuses() {
        return repository.findAllByOrderByIdAsc();
    }

    public PurchaseOrderStatusEntity getPurchaseOrderStatus(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(STATUS_NOT_FOUND_ERR_MSG)));
    }

    public PurchaseOrderStatusEntity getFirstPurchaseOrderStatus() {
        return repository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(STATUS_NOT_FOUND_ERR_MSG)));
    }
}
