package com.djccnt15.northwind.domain.company.business;

import com.djccnt15.northwind.domain.company.converter.CompanyConverter;
import com.djccnt15.northwind.domain.company.converter.CompanyTypeConverter;
import com.djccnt15.northwind.domain.company.converter.ContactConverter;
import com.djccnt15.northwind.domain.company.converter.OrderSummaryConverter;
import com.djccnt15.northwind.domain.company.converter.PurchaseOrderSummaryConverter;
import com.djccnt15.northwind.domain.company.model.CompanyCreateReq;
import com.djccnt15.northwind.domain.company.model.CompanyRes;
import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.company.model.ContactCreateReq;
import com.djccnt15.northwind.domain.company.model.ContactRes;
import com.djccnt15.northwind.domain.company.model.OrderSummaryRes;
import com.djccnt15.northwind.domain.company.model.PurchaseOrderSummaryRes;
import com.djccnt15.northwind.domain.company.service.CompanyOrderService;
import com.djccnt15.northwind.domain.company.service.CompanyPurchaseOrderService;
import com.djccnt15.northwind.domain.company.service.CompanyService;
import com.djccnt15.northwind.domain.company.service.CompanyTypeService;
import com.djccnt15.northwind.domain.company.service.ContactService;
import com.djccnt15.northwind.domain.tax.converter.TaxStatusConverter;
import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
import com.djccnt15.northwind.domain.tax.service.TaxStatusService;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class CompanyBusiness {

    private final CompanyService companyService;
    private final CompanyConverter companyConverter;
    private final CompanyTypeService companyTypeService;
    private final CompanyTypeConverter companyTypeConverter;
    private final TaxStatusService taxStatusService;
    private final TaxStatusConverter taxStatusConverter;
    private final ContactService contactService;
    private final ContactConverter contactConverter;
    private final CompanyOrderService companyOrderService;
    private final OrderSummaryConverter orderSummaryConverter;
    private final CompanyPurchaseOrderService companyPurchaseOrderService;
    private final PurchaseOrderSummaryConverter purchaseOrderSummaryConverter;

    public List<CompanyTypeRes> getCompanyTypes() {
        return companyTypeService.getCompanyTypes().stream()
            .map(companyTypeConverter::toResponse).toList();
    }

    public List<TaxStatusRes> getTaxStatuses() {
        return taxStatusService.getTaxStatuses().stream()
            .map(taxStatusConverter::toResponse).toList();
    }

    public Page<CompanyRes> getCompanies(int page, int size, Long type, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by("name"));
        return companyService.getCompanies(kw, type, pageable).map(companyConverter::toResponse);
    }

    public CompanyRes getCompany(Long id) {
        return companyConverter.toResponse(companyService.getCompany(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyRes createCompany(CompanyCreateReq request) {
        companyService.validateCompany(request);
        var companyType = companyTypeService.getCompanyType(request.getCompanyTypeId());
        var taxStatus = taxStatusService.getTaxStatus(request.getTaxStatusId());
        var entity = companyService.createCompany(request, companyType, taxStatus);
        return companyConverter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyRes updateCompany(Long id, CompanyCreateReq request) {
        companyService.validateCompany(id, request);
        var entity = companyService.getCompany(id);
        var companyType = companyTypeService.getCompanyType(request.getCompanyTypeId());
        var taxStatus = taxStatusService.getTaxStatus(request.getTaxStatusId());
        companyService.updateCompany(entity, request, companyType, taxStatus);
        return companyConverter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCompany(Long id) {
        var entity = companyService.getCompany(id);
        companyService.deleteCompany(entity);
    }

    public List<ContactRes> getContacts(Long companyId) {
        companyService.getCompany(companyId);
        return contactService.getContacts(companyId).stream()
            .map(contactConverter::toResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ContactRes createContact(Long companyId, ContactCreateReq request) {
        var company = companyService.getCompany(companyId);
        contactService.validateContact(request);
        var entity = contactService.createContact(request, company);
        return contactConverter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ContactRes updateContact(Long companyId, Long contactId, ContactCreateReq request) {
        var entity = contactService.getContact(companyId, contactId);
        contactService.validateContact(contactId, request);
        contactService.updateContact(entity, request);
        return contactConverter.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(Long companyId, Long contactId) {
        var entity = contactService.getContact(companyId, contactId);
        contactService.deleteContact(entity);
    }

    public List<OrderSummaryRes> getOrders(Long companyId) {
        companyService.getCompany(companyId);
        return companyOrderService.getOrdersByCustomer(companyId).stream()
            .map(orderSummaryConverter::toResponse).toList();
    }

    public List<PurchaseOrderSummaryRes> getPurchaseOrders(Long companyId) {
        companyService.getCompany(companyId);
        return companyPurchaseOrderService.getPurchaseOrdersByVendor(companyId).stream()
            .map(purchaseOrderSummaryConverter::toResponse).toList();
    }
}
