package com.djccnt15.northwind.domain.company.controller;

import com.djccnt15.northwind.domain.company.business.CompanyBusiness;
import com.djccnt15.northwind.domain.company.model.CompanyCreateReq;
import com.djccnt15.northwind.domain.company.model.CompanyRes;
import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.company.model.ContactCreateReq;
import com.djccnt15.northwind.domain.company.model.ContactRes;
import com.djccnt15.northwind.domain.company.model.OrderSummaryRes;
import com.djccnt15.northwind.domain.company.model.PurchaseOrderSummaryRes;
import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
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
@PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY')")
@RequiredArgsConstructor
public class CompanyApiController {

    private final CompanyBusiness business;

    @GetMapping("/company-types")
    public ResponseEntity<Api<List<CompanyTypeRes>>> getCompanyTypes() {
        var response = business.getCompanyTypes();
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/tax-statuses")
    public ResponseEntity<Api<List<TaxStatusRes>>> getTaxStatuses() {
        var response = business.getTaxStatuses();
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/companies")
    public ResponseEntity<Api<Page<CompanyRes>>> getCompanies(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Long type,
        @RequestParam(defaultValue = "") String keyword
    ) {
        var response = business.getCompanies(page, size, type, keyword);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Api<CompanyRes>> getCompany(@PathVariable Long id) {
        var response = business.getCompany(id);
        return ResponseEntity.ok(Api.OK(response));
    }

    @PostMapping("/companies")
    public ResponseEntity<Api<CompanyRes>> createCompany(
        @Validated @RequestBody CompanyCreateReq request
    ) {
        var response = business.createCompany(request);
        return ResponseEntity.ok(Api.CREATED(response));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Api<CompanyRes>> updateCompany(
        @PathVariable Long id,
        @Validated @RequestBody CompanyCreateReq request
    ) {
        var response = business.updateCompany(id, request);
        return ResponseEntity.ok(Api.OK(response));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Api<?>> deleteCompany(@PathVariable Long id) {
        business.deleteCompany(id);
        return ResponseEntity.ok(Api.OK(null));
    }

    @GetMapping("/companies/{id}/contacts")
    public ResponseEntity<Api<List<ContactRes>>> getContacts(@PathVariable Long id) {
        var response = business.getContacts(id);
        return ResponseEntity.ok(Api.OK(response));
    }

    @PostMapping("/companies/{id}/contacts")
    public ResponseEntity<Api<ContactRes>> createContact(
        @PathVariable Long id,
        @Validated @RequestBody ContactCreateReq request
    ) {
        var response = business.createContact(id, request);
        return ResponseEntity.ok(Api.CREATED(response));
    }

    @PutMapping("/companies/{id}/contacts/{contactId}")
    public ResponseEntity<Api<ContactRes>> updateContact(
        @PathVariable Long id,
        @PathVariable Long contactId,
        @Validated @RequestBody ContactCreateReq request
    ) {
        var response = business.updateContact(id, contactId, request);
        return ResponseEntity.ok(Api.OK(response));
    }

    @DeleteMapping("/companies/{id}/contacts/{contactId}")
    public ResponseEntity<Api<?>> deleteContact(
        @PathVariable Long id,
        @PathVariable Long contactId
    ) {
        business.deleteContact(id, contactId);
        return ResponseEntity.ok(Api.OK(null));
    }

    @GetMapping("/companies/{id}/orders")
    public ResponseEntity<Api<List<OrderSummaryRes>>> getOrders(@PathVariable Long id) {
        var response = business.getOrders(id);
        return ResponseEntity.ok(Api.OK(response));
    }

    @GetMapping("/companies/{id}/purchase-orders")
    public ResponseEntity<Api<List<PurchaseOrderSummaryRes>>> getPurchaseOrders(@PathVariable Long id) {
        var response = business.getPurchaseOrders(id);
        return ResponseEntity.ok(Api.OK(response));
    }
}
