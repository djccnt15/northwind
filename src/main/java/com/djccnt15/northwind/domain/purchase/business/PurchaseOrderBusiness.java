package com.djccnt15.northwind.domain.purchase.business;

import com.djccnt15.northwind.db.entity.EmployeeEntity;
import com.djccnt15.northwind.domain.company.converter.CompanyTypeConverter;
import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.company.service.CompanyService;
import com.djccnt15.northwind.domain.company.service.CompanyTypeService;
import com.djccnt15.northwind.domain.purchase.converter.ProductCostOptionConverter;
import com.djccnt15.northwind.domain.purchase.converter.PurchaseOrderConverter;
import com.djccnt15.northwind.domain.purchase.converter.PurchaseOrderDetailConverter;
import com.djccnt15.northwind.domain.purchase.converter.PurchaseOrderStatusConverter;
import com.djccnt15.northwind.domain.purchase.converter.VendorOptionConverter;
import com.djccnt15.northwind.domain.purchase.model.CompanyOptionRes;
import com.djccnt15.northwind.domain.purchase.model.ProductCostOptionRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderDetailCreateReq;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderListRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusRes;
import com.djccnt15.northwind.domain.purchase.model.PurchaseOrderStatusUpdateReq;
import com.djccnt15.northwind.domain.purchase.service.PurchaseOrderService;
import com.djccnt15.northwind.domain.purchase.service.PurchaseOrderStatusService;
import com.djccnt15.northwind.domain.product.service.ProductService;
import com.djccnt15.northwind.domain.user.service.EmployeeService;
import com.djccnt15.northwind.global.annotation.Business;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.djccnt15.northwind.domain.purchase.service.PurchaseOrderService.CODE_APPROVED;
import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderErrorConst.EMPLOYEE_REQUIRED_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;

@Slf4j
@Business
@RequiredArgsConstructor
public class PurchaseOrderBusiness {

    private static final int LOOKUP_PAGE_SIZE = 100;

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderConverter purchaseOrderConverter;
    private final PurchaseOrderDetailConverter purchaseOrderDetailConverter;
    private final PurchaseOrderStatusService purchaseOrderStatusService;
    private final PurchaseOrderStatusConverter purchaseOrderStatusConverter;

    // cross-domain reuse for lookups under PURCHASE authority
    private final CompanyService companyService;
    private final VendorOptionConverter vendorOptionConverter;
    private final CompanyTypeService companyTypeService;
    private final CompanyTypeConverter companyTypeConverter;
    private final ProductService productService;
    private final ProductCostOptionConverter productCostOptionConverter;
    private final EmployeeService employeeService;
    private final MessageUtil messageUtil;

    // ----- list / detail -----

    public Page<PurchaseOrderListRes> getPurchaseOrders(
        int page, int size, Long statusId, LocalDate dateFrom, LocalDate dateTo, String keyword
    ) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedDate"));
        var result = purchaseOrderService.getPurchaseOrders(kw, statusId, dateFrom, dateTo, pageable);
        var ids = result.getContent().stream().map(po -> po.getId()).toList();
        var totals = purchaseOrderService.getTotalAmounts(ids);
        return result.map(po -> purchaseOrderConverter.toListResponse(po, totals.get(po.getId())));
    }

    public PurchaseOrderRes getPurchaseOrder(Long id) {
        return purchaseOrderConverter.toResponse(purchaseOrderService.getPurchaseOrder(id));
    }

    // ----- create -----

    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrderRes createPurchaseOrder(PurchaseOrderCreateReq request, UserSession userSession) {
        var vendor = companyService.getCompany(request.getVendorId());
        var submittedBy = resolveEmployee(userSession);
        var status = purchaseOrderStatusService.getFirstPurchaseOrderStatus();

        var purchaseOrder = purchaseOrderConverter.toEntity(request, vendor, submittedBy, status);

        var productIds = request.getPurchaseOrderDetails().stream()
            .map(PurchaseOrderDetailCreateReq::getProductId).toList();
        var productMap = productService.getProducts(productIds);
        request.getPurchaseOrderDetails().forEach(detailReq -> {
            var product = productMap.get(detailReq.getProductId());
            var detail = purchaseOrderDetailConverter.toEntity(detailReq, product, purchaseOrder);
            purchaseOrder.getPurchaseOrderDetails().add(detail);
        });

        var saved = purchaseOrderService.createPurchaseOrder(purchaseOrder);
        // re-read with relations to build a complete response
        return purchaseOrderConverter.toResponse(purchaseOrderService.getPurchaseOrder(saved.getId()));
    }

    // ----- status transitions -----

    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrderRes updatePurchaseOrderStatus(
        Long purchaseOrderId, PurchaseOrderStatusUpdateReq request, UserSession userSession
    ) {
        var purchaseOrder = purchaseOrderService.getPurchaseOrder(purchaseOrderId);
        var newStatus = purchaseOrderStatusService.getPurchaseOrderStatus(request.getStatusId());
        // record the current user as approver on APPROVED transitions
        var approver = CODE_APPROVED.equals(newStatus.getCode())
            ? resolveEmployee(userSession)
            : null;
        purchaseOrderService.updatePurchaseOrderStatus(purchaseOrder, newStatus, approver, request);
        return purchaseOrderConverter.toResponse(purchaseOrderService.getPurchaseOrder(purchaseOrderId));
    }

    private EmployeeEntity resolveEmployee(UserSession userSession) {
        var appUser = purchaseOrderService.getUserReference(userSession.getId());
        return employeeService.getEmployee(appUser)
            .orElseThrow(() -> new ApiException(BAD_REQUEST, messageUtil.getMessage(EMPLOYEE_REQUIRED_ERR_MSG)));
    }

    // ----- lookups -----

    public List<PurchaseOrderStatusRes> getPurchaseOrderStatuses() {
        return purchaseOrderStatusService.getPurchaseOrderStatuses().stream()
            .map(purchaseOrderStatusConverter::toResponse).toList();
    }

    public List<CompanyTypeRes> getCompanyTypes() {
        return companyTypeService.getCompanyTypes().stream()
            .map(companyTypeConverter::toResponse).toList();
    }

    public List<CompanyOptionRes> getVendorOptions(Long typeId, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(0, LOOKUP_PAGE_SIZE, Sort.by("name"));
        return companyService.getCompanies(kw, typeId, pageable).getContent().stream()
            .map(vendorOptionConverter::toResponse).toList();
    }

    public List<ProductCostOptionRes> getProductOptions(String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(0, LOOKUP_PAGE_SIZE, Sort.by("name"));
        return productService.getProducts(kw, null, false, pageable).getContent().stream()
            .map(productCostOptionConverter::toResponse).toList();
    }
}
