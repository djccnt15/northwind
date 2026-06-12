package com.djccnt15.northwind.domain.order.controller;

import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.order.business.OrderBusiness;
import com.djccnt15.northwind.domain.order.model.CompanyOptionRes;
import com.djccnt15.northwind.domain.order.model.OrderCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusRes;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusUpdateReq;
import com.djccnt15.northwind.domain.order.model.OrderListRes;
import com.djccnt15.northwind.domain.order.model.OrderRes;
import com.djccnt15.northwind.domain.order.model.OrderStatusRes;
import com.djccnt15.northwind.domain.order.model.OrderStatusUpdateReq;
import com.djccnt15.northwind.domain.order.model.ProductOptionRes;
import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1)
@PreAuthorize("hasAnyAuthority('ADMIN', 'ORDER')")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderBusiness business;

    // ----- status master lookups -----

    @GetMapping("/order-statuses")
    public ResponseEntity<Api<List<OrderStatusRes>>> getOrderStatuses() {
        return ResponseEntity.ok(Api.OK(business.getOrderStatuses()));
    }

    @GetMapping("/order-detail-statuses")
    public ResponseEntity<Api<List<OrderDetailStatusRes>>> getOrderDetailStatuses() {
        return ResponseEntity.ok(Api.OK(business.getOrderDetailStatuses()));
    }

    // ----- cross-domain lookups for the order-create form -----

    @GetMapping("/orders/company-types")
    public ResponseEntity<Api<List<CompanyTypeRes>>> getCompanyTypes() {
        return ResponseEntity.ok(Api.OK(business.getCompanyTypes()));
    }

    @GetMapping("/orders/companies")
    public ResponseEntity<Api<List<CompanyOptionRes>>> getCompanyOptions(
        @RequestParam(required = false) Long type,
        @RequestParam(defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(Api.OK(business.getCompanyOptions(type, keyword)));
    }

    @GetMapping("/orders/products")
    public ResponseEntity<Api<List<ProductOptionRes>>> getProductOptions(
        @RequestParam(defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(Api.OK(business.getProductOptions(keyword)));
    }

    @GetMapping("/orders/tax-statuses")
    public ResponseEntity<Api<List<TaxStatusRes>>> getTaxStatuses() {
        return ResponseEntity.ok(Api.OK(business.getTaxStatuses()));
    }

    // ----- orders -----

    @GetMapping("/orders")
    public ResponseEntity<Api<Page<OrderListRes>>> getOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Long status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(defaultValue = "") String keyword
    ) {
        var response = business.getOrders(page, size, status, dateFrom, dateTo, keyword);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Api<OrderRes>> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(Api.OK(business.getOrder(id)));
    }

    @PostMapping("/orders")
    public ResponseEntity<Api<OrderRes>> createOrder(
        @Validated @RequestBody OrderCreateReq request,
        @AuthenticationPrincipal UserSession userSession
    ) {
        return ResponseEntity.ok(Api.CREATED(business.createOrder(request, userSession)));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<Api<OrderRes>> updateOrderStatus(
        @PathVariable Long id,
        @Validated @RequestBody OrderStatusUpdateReq request
    ) {
        return ResponseEntity.ok(Api.OK(business.updateOrderStatus(id, request)));
    }

    @PatchMapping("/orders/{id}/details/{detailId}/status")
    public ResponseEntity<Api<OrderRes>> updateOrderDetailStatus(
        @PathVariable Long id,
        @PathVariable Long detailId,
        @Validated @RequestBody OrderDetailStatusUpdateReq request
    ) {
        return ResponseEntity.ok(Api.OK(business.updateOrderDetailStatus(id, detailId, request)));
    }
}
