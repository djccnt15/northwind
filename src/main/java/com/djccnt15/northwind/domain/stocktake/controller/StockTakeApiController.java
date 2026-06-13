package com.djccnt15.northwind.domain.stocktake.controller;

import com.djccnt15.northwind.domain.stocktake.business.StockTakeBusiness;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeRowRes;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeSaveReq;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1)
@PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK')")
@RequiredArgsConstructor
public class StockTakeApiController {

    private final StockTakeBusiness business;

    @GetMapping("/stock-takes")
    public ResponseEntity<Api<Page<StockTakeRowRes>>> getStockTakeRows(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(Api.OK(business.getStockTakeRows(keyword, page, size)));
    }

    @PostMapping("/stock-takes")
    public ResponseEntity<Api<List<StockTakeRowRes>>> saveStockTakes(
        @Validated @RequestBody StockTakeSaveReq request
    ) {
        return ResponseEntity.ok(Api.OK(business.saveStockTakes(request)));
    }
}
