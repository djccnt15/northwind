package com.djccnt15.northwind.domain.order.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.djccnt15.northwind.domain.order.validation.OrderModelConst.STATUS_NOT_NULL_MSG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailStatusUpdateReq {

    @NotNull(message = STATUS_NOT_NULL_MSG)
    private Long statusId;
}
