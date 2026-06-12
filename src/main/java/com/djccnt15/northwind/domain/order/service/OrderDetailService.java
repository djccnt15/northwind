package com.djccnt15.northwind.domain.order.service;

import com.djccnt15.northwind.db.entity.OrderDetailEntity;
import com.djccnt15.northwind.db.entity.OrderDetailStatusEntity;
import com.djccnt15.northwind.db.repository.OrderDetailRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.djccnt15.northwind.domain.order.validation.OrderDetailErrorConst.NOT_FOUND_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepo repository;
    private final MessageUtil messageUtil;

    public OrderDetailEntity getOrderDetail(Long orderId, Long detailId) {
        var entity = repository.findWithRelationById(detailId)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
        if (!entity.getOrder().getId().equals(orderId)) {
            throw new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG));
        }
        return entity;
    }

    public void updateOrderDetailStatus(OrderDetailEntity entity, OrderDetailStatusEntity newStatus) {
        entity.setOrderDetailStatus(newStatus);
        repository.save(entity);
    }
}
