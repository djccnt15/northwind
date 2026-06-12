package com.djccnt15.northwind.domain.order.service;

import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.db.repository.OrderDetailStatusRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.domain.order.validation.OrderDetailErrorConst.STATUS_NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailStatusService {

    private final OrderDetailStatusRepo repository;
    private final MessageUtil messageUtil;

    public List<OrderDetailStatusEntity> getOrderDetailStatuses() {
        return repository.findAllByOrderByIdAsc();
    }

    public OrderDetailStatusEntity getOrderDetailStatus(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(STATUS_NOT_FOUND_ERR_MSG)));
    }

    public OrderDetailStatusEntity getFirstOrderDetailStatus() {
        return repository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(STATUS_NOT_FOUND_ERR_MSG)));
    }
}
