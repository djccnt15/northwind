# 구현 계획

## 백엔드 작업 항목

### 1. ProductService — 배치 조회 메서드 추가

**파일**: `src/main/java/com/djccnt15/northwind/domain/product/service/ProductService.java`

`getProducts(List<Long> ids)` 메서드 추가:
- `repository.findAllById(ids)`로 1회 조회
- 결과를 `Map<Long, ProductEntity>`로 변환 (`Collectors.toMap(ProductEntity::getId, Function.identity())`)
- 요청한 ID 중 조회 결과에 없는 것이 있으면 `ApiException(NOT_FOUND)` 발생 (기존 `getProduct()` 동작 유지)
- `@EntityGraph` 불필요 — 생성 컨텍스트에서 `productCategory` 접근 없음

### 2. OrderBusiness.createOrder() — 배치 조회 적용

**파일**: `src/main/java/com/djccnt15/northwind/domain/order/business/OrderBusiness.java`

변경 전:
```java
request.getOrderDetails().forEach(detailReq -> {
    var product = productService.getProduct(detailReq.getProductId());
    var detail = orderDetailConverter.toEntity(detailReq, product, defaultDetailStatus, order);
    order.getOrderDetails().add(detail);
});
```

변경 후:
```java
var productIds = request.getOrderDetails().stream()
    .map(OrderDetailCreateReq::getProductId).toList();
var productMap = productService.getProducts(productIds);

request.getOrderDetails().forEach(detailReq -> {
    var product = productMap.get(detailReq.getProductId());
    var detail = orderDetailConverter.toEntity(detailReq, product, defaultDetailStatus, order);
    order.getOrderDetails().add(detail);
});
```

### 3. PurchaseOrderBusiness.createPurchaseOrder() — 배치 조회 적용

**파일**: `src/main/java/com/djccnt15/northwind/domain/purchase/business/PurchaseOrderBusiness.java`

동일 패턴 적용:
```java
var productIds = request.getPurchaseOrderDetails().stream()
    .map(PurchaseOrderDetailCreateReq::getProductId).toList();
var productMap = productService.getProducts(productIds);

request.getPurchaseOrderDetails().forEach(detailReq -> {
    var product = productMap.get(detailReq.getProductId());
    ...
});
```

### 4. StockTakeBusiness.saveStockTakes() — 배치 조회 적용

**파일**: `src/main/java/com/djccnt15/northwind/domain/stocktake/business/StockTakeBusiness.java`

변경 전:
```java
return request.getItems().stream()
    .map(item -> saveItem(request, item)).toList();
// saveItem 내부: productService.getProduct(item.getProductId())
```

변경 후:
```java
var productIds = request.getItems().stream()
    .map(StockTakeItemReq::getProductId).toList();
var productMap = productService.getProducts(productIds);

return request.getItems().stream()
    .map(item -> {
        var product = productMap.get(item.getProductId());
        var saved = stockTakeService.upsert(product, request.getStockTakeDate(), item.getQuantityOnHand());
        return stockTakeConverter.toRowResponse(product, saved.getExpectedQuantity(), saved.getQuantityOnHand());
    }).toList();
```

`saveItem` private 메서드는 인라인 또는 시그니처 변경 (`ProductEntity product`를 인자로 받도록).

## 참고 패턴

- `StockTakeBusiness.getStockTakeRows()` (35~49행): productIds 추출 → `getLatestQuantities(productIds)` / `getDraftQuantities(productIds, ...)` 배치 조회 → Map → `products.map(product -> converter.toRowResponse(product, latest.get(...), drafts.get(...)))` 패턴
- `OrderBusiness.getOrders()` (70~78행): `result.getContent().stream().map(o -> o.getId()).toList()` → `orderService.getTotalAmounts(ids)` 배치 → Map

## doc/ 영향 범위

- doc/StoryBoard.md: 없음 — 기존 기능의 내부 리팩토링이며 화면/API 변경 없음
- doc/PRD.md / doc/EDR.md: 없음

## QA 중점 검토 항목

- 배치 조회 시 존재하지 않는 productId에 대한 NOT_FOUND 예외 발생 확인
- 기존 테스트가 모두 통과하는지 확인 (API 계약 변경 없으므로 기존 테스트로 충분)
- Business 레이어에서 루프 내 개별 쿼리 호출이 완전히 제거되었는지 확인
