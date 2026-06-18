package com.djccnt15.northwind.domain.order.business;

import com.djccnt15.northwind.domain.company.converter.CompanyTypeConverter;
import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.company.service.CompanyService;
import com.djccnt15.northwind.domain.company.service.CompanyTypeService;
import com.djccnt15.northwind.domain.order.converter.CompanyOptionConverter;
import com.djccnt15.northwind.domain.order.converter.OrderConverter;
import com.djccnt15.northwind.domain.order.converter.OrderDetailConverter;
import com.djccnt15.northwind.domain.order.converter.OrderDetailStatusConverter;
import com.djccnt15.northwind.domain.order.converter.OrderStatusConverter;
import com.djccnt15.northwind.domain.order.converter.ProductOptionConverter;
import com.djccnt15.northwind.domain.order.model.CompanyOptionRes;
import com.djccnt15.northwind.domain.order.model.OrderCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailCreateReq;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusRes;
import com.djccnt15.northwind.domain.order.model.OrderDetailStatusUpdateReq;
import com.djccnt15.northwind.domain.order.model.OrderListRes;
import com.djccnt15.northwind.domain.order.model.OrderRes;
import com.djccnt15.northwind.domain.order.model.OrderStatusRes;
import com.djccnt15.northwind.domain.order.model.OrderStatusUpdateReq;
import com.djccnt15.northwind.domain.order.model.ProductOptionRes;
import com.djccnt15.northwind.domain.order.service.OrderDetailService;
import com.djccnt15.northwind.domain.order.service.OrderDetailStatusService;
import com.djccnt15.northwind.domain.order.service.OrderService;
import com.djccnt15.northwind.domain.order.service.OrderStatusService;
import com.djccnt15.northwind.domain.product.service.ProductService;
import com.djccnt15.northwind.domain.tax.converter.TaxStatusConverter;
import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
import com.djccnt15.northwind.domain.tax.service.TaxStatusService;
import com.djccnt15.northwind.global.annotation.Business;
import com.djccnt15.northwind.global.config.security.model.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class OrderBusiness {

    private static final int LOOKUP_PAGE_SIZE = 100;

    private final OrderService orderService;
    private final OrderConverter orderConverter;
    private final OrderDetailService orderDetailService;
    private final OrderDetailConverter orderDetailConverter;
    private final OrderStatusService orderStatusService;
    private final OrderStatusConverter orderStatusConverter;
    private final OrderDetailStatusService orderDetailStatusService;
    private final OrderDetailStatusConverter orderDetailStatusConverter;

    // cross-domain reuse for lookups under ORDER authority
    private final CompanyService companyService;
    private final CompanyOptionConverter companyOptionConverter;
    private final CompanyTypeService companyTypeService;
    private final CompanyTypeConverter companyTypeConverter;
    private final ProductService productService;
    private final ProductOptionConverter productOptionConverter;
    private final TaxStatusService taxStatusService;
    private final TaxStatusConverter taxStatusConverter;

    // ----- list / detail -----

    public Page<OrderListRes> getOrders(
        int page, int size, Long statusId, LocalDate dateFrom, LocalDate dateTo, String keyword
    ) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        var result = orderService.getOrders(kw, statusId, dateFrom, dateTo, pageable);
        var ids = result.getContent().stream().map(o -> o.getId()).toList();
        var totals = orderService.getTotalAmounts(ids);
        return result.map(o -> orderConverter.toListResponse(o, totals.get(o.getId())));
    }

    public OrderRes getOrder(Long id) {
        return orderConverter.toResponse(orderService.getOrder(id));
    }

    // ----- create -----

    @Transactional(rollbackFor = Exception.class)
    public OrderRes createOrder(OrderCreateReq request, UserSession userSession) {
        var customer = companyService.getCompany(request.getCustomerId());
        var shipper = request.getShipperId() != null
            ? companyService.getCompany(request.getShipperId())
            : null;
        var taxStatus = taxStatusService.getTaxStatus(request.getTaxStatusId());
        var orderStatus = orderStatusService.getFirstOrderStatus();
        var appUser = orderService.getUserReference(userSession.getId());

        var order = orderConverter.toEntity(request, customer, shipper, taxStatus, orderStatus, appUser);

        var defaultDetailStatus = orderDetailStatusService.getFirstOrderDetailStatus();
        var productIds = request.getOrderDetails().stream()
            .map(OrderDetailCreateReq::getProductId).toList();
        var productMap = productService.getProducts(productIds);
        request.getOrderDetails().forEach(detailReq -> {
            var product = productMap.get(detailReq.getProductId());
            var detail = orderDetailConverter.toEntity(detailReq, product, defaultDetailStatus, order);
            order.getOrderDetails().add(detail);
        });

        var saved = orderService.createOrder(order);
        // re-read with relations to build a complete response
        return orderConverter.toResponse(orderService.getOrder(saved.getId()));
    }

    // ----- status transitions -----

    @Transactional(rollbackFor = Exception.class)
    public OrderRes updateOrderStatus(Long orderId, OrderStatusUpdateReq request) {
        var order = orderService.getOrder(orderId);
        var newStatus = orderStatusService.getOrderStatus(request.getStatusId());
        orderService.updateOrderStatus(order, newStatus);
        return orderConverter.toResponse(orderService.getOrder(orderId));
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderRes updateOrderDetailStatus(Long orderId, Long detailId, OrderDetailStatusUpdateReq request) {
        var detail = orderDetailService.getOrderDetail(orderId, detailId);
        var newStatus = orderDetailStatusService.getOrderDetailStatus(request.getStatusId());
        orderDetailService.updateOrderDetailStatus(detail, newStatus);
        return orderConverter.toResponse(orderService.getOrder(orderId));
    }

    // ----- lookups -----

    public List<OrderStatusRes> getOrderStatuses() {
        return orderStatusService.getOrderStatuses().stream()
            .map(orderStatusConverter::toResponse).toList();
    }

    public List<OrderDetailStatusRes> getOrderDetailStatuses() {
        return orderDetailStatusService.getOrderDetailStatuses().stream()
            .map(orderDetailStatusConverter::toResponse).toList();
    }

    public List<CompanyTypeRes> getCompanyTypes() {
        return companyTypeService.getCompanyTypes().stream()
            .map(companyTypeConverter::toResponse).toList();
    }

    public List<CompanyOptionRes> getCompanyOptions(Long typeId, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(0, LOOKUP_PAGE_SIZE, Sort.by("name"));
        return companyService.getCompanies(kw, typeId, pageable).getContent().stream()
            .map(companyOptionConverter::toResponse).toList();
    }

    public List<ProductOptionRes> getProductOptions(String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(0, LOOKUP_PAGE_SIZE, Sort.by("name"));
        return productService.getProducts(kw, null, false, pageable).getContent().stream()
            .map(productOptionConverter::toResponse).toList();
    }

    public List<TaxStatusRes> getTaxStatuses() {
        return taxStatusService.getTaxStatuses().stream()
            .map(taxStatusConverter::toResponse).toList();
    }
}
