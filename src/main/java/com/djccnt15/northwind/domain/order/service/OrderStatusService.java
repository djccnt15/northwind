package com.djccnt15.northwind.domain.order.service;

import com.djccnt15.northwind.db.entity.OrderStatusEntity;
import com.djccnt15.northwind.db.repository.OrderStatusRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.domain.order.validation.OrderErrorConst.STATUS_NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderStatusRepo repository;
    private final MessageUtil messageUtil;

    public List<OrderStatusEntity> getOrderStatuses() {
        return repository.findAllByOrderByIdAsc();
    }

    public OrderStatusEntity getOrderStatus(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(STATUS_NOT_FOUND_ERR_MSG)));
    }

    public OrderStatusEntity getFirstOrderStatus() {
        return repository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(STATUS_NOT_FOUND_ERR_MSG)));
    }
}
