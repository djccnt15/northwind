package com.djccnt15.northwind.domain.purchase.controller;

import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.purchase.business.PurchaseOrderBusiness;
import com.djccnt15.northwind.domain.purchase.model.CompanyOptionRes;
import com.djccnt15.northwind.domain.purchase.model.ProductCostOptionRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderListRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusUpdateReq;
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
@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")
@RequiredArgsConstructor
public class PurchaseOrderApiController {

    private final PurchaseOrderBusiness business;

    // ----- status master lookups -----

    @GetMapping("/purchase-order-statuses")
    public ResponseEntity<Api<List<PurchaseOrderStatusRes>>> getPurchaseOrderStatuses() {
        return ResponseEntity.ok(Api.OK(business.getPurchaseOrderStatuses()));
    }

    // ----- cross-domain lookups for the purchase-order-create form -----

    @GetMapping("/purchase-orders/company-types")
    public ResponseEntity<Api<List<CompanyTypeRes>>> getCompanyTypes() {
        return ResponseEntity.ok(Api.OK(business.getCompanyTypes()));
    }

    @GetMapping("/purchase-orders/companies")
    public ResponseEntity<Api<List<CompanyOptionRes>>> getVendorOptions(
        @RequestParam(required = false) Long type,
        @RequestParam(defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(Api.OK(business.getVendorOptions(type, keyword)));
    }

    @GetMapping("/purchase-orders/products")
    public ResponseEntity<Api<List<ProductCostOptionRes>>> getProductOptions(
        @RequestParam(defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(Api.OK(business.getProductOptions(keyword)));
    }

    // ----- purchase orders -----

    @GetMapping("/purchase-orders")
    public ResponseEntity<Api<Page<PurchaseOrderListRes>>> getPurchaseOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Long status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(defaultValue = "") String keyword
    ) {
        var response = business.getPurchaseOrders(page, size, status, dateFrom, dateTo, keyword);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<Api<PurchaseOrderRes>> getPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(Api.OK(business.getPurchaseOrder(id)));
    }

    @PostMapping("/purchase-orders")
    public ResponseEntity<Api<PurchaseOrderRes>> createPurchaseOrder(
        @Validated @RequestBody PurchaseOrderCreateReq request,
        @AuthenticationPrincipal UserSession userSession
    ) {
        return ResponseEntity.ok(Api.CREATED(business.createPurchaseOrder(request, userSession)));
    }

    @PatchMapping("/purchase-orders/{id}/status")
    public ResponseEntity<Api<PurchaseOrderRes>> updatePurchaseOrderStatus(
        @PathVariable Long id,
        @Validated @RequestBody PurchaseOrderStatusUpdateReq request,
        @AuthenticationPrincipal UserSession userSession
    ) {
        return ResponseEntity.ok(Api.OK(business.updatePurchaseOrderStatus(id, request, userSession)));
    }
}
