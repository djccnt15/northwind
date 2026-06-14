package com.djccnt15.northwind.domain.stocktake.business;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.domain.product.service.ProductService;
import com.djccnt15.northwind.domain.stocktake.converter.StockTakeConverter;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeItemReq;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeRowRes;
import com.djccnt15.northwind.domain.stocktake.model.StockTakeSaveReq;
import com.djccnt15.northwind.domain.stocktake.service.StockTakeService;
import com.djccnt15.northwind.global.annotation.Business;
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
public class StockTakeBusiness {

    private final ProductService productService;
    private final StockTakeService stockTakeService;
    private final StockTakeConverter stockTakeConverter;

    /**
     * Page of stock-take rows: paginate active products, then enrich each with its computer stock
     * (latest stock-take quantity, 0 when never counted) and today's draft count (null when absent).
     * Two extra queries per page (latest + draft) keep this free of N+1.
     */
    public Page<StockTakeRowRes> getStockTakeRows(String keyword, int page, int size) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by("id"));
        // only count products still in the catalog (discontinued = false)
        var products = productService.getProducts(kw, null, false, pageable);

        var productIds = products.getContent().stream().map(ProductEntity::getId).toList();
        var latest = stockTakeService.getLatestQuantities(productIds);
        var drafts = stockTakeService.getDraftQuantities(productIds, LocalDate.now());

        return products.map(product -> stockTakeConverter.toRowResponse(
            product,
            latest.getOrDefault(product.getId(), 0L),
            drafts.get(product.getId())
        ));
    }

    /**
     * Batch upsert of physical counts. Each item resolves its product, upserts the (product, date)
     * record, and is returned as a refreshed row (expectedQuantity is the value fixed at create time).
     */
    @Transactional(rollbackFor = Exception.class)
    public List<StockTakeRowRes> saveStockTakes(StockTakeSaveReq request) {
        return request.getItems().stream()
            .map(item -> saveItem(request, item)).toList();
    }

    private StockTakeRowRes saveItem(StockTakeSaveReq request, StockTakeItemReq item) {
        var product = productService.getProduct(item.getProductId());
        var saved = stockTakeService.upsert(product, request.getStockTakeDate(), item.getQuantityOnHand());
        return stockTakeConverter.toRowResponse(
            product,
            saved.getExpectedQuantity(),
            saved.getQuantityOnHand()
        );
    }
}
