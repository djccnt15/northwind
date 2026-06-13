package com.djccnt15.northwind.domain.stocktake.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.djccnt15.northwind.domain.stocktake.validation.StockTakeModelConst.DATE_NOT_NULL_MSG;
import static com.djccnt15.northwind.domain.stocktake.validation.StockTakeModelConst.ITEMS_NOT_EMPTY_MSG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTakeSaveReq {

    @NotNull(message = DATE_NOT_NULL_MSG)
    private LocalDate stockTakeDate;

    @Valid
    @NotEmpty(message = ITEMS_NOT_EMPTY_MSG)
    private List<StockTakeItemReq> items;
}
