# 백엔드 API 계약

## 작업 요약

N+1 쿼리 방지 리팩토링. 주문 생성 / 구매주문 생성 / 재고실사 저장 시 루프 내 개별
`productService.getProduct()` 호출(N번)을 `productService.getProducts(List<Long>)` 배치 조회(1번)로 대체했다.

**내부 리팩토링이며 API 계약(요청/응답 형식)은 변경되지 않는다.** 프론트엔드 영향 없음.

## 생성/수정된 파일

- `src/main/java/com/djccnt15/northwind/domain/product/service/ProductService.java`
  - `getProducts(List<Long> ids)` 배치 조회 메서드 추가
- `src/main/java/com/djccnt15/northwind/domain/order/business/OrderBusiness.java`
  - `createOrder()` — 루프 전 productIds 추출 → 배치 조회 → `Map.get()` 사용
- `src/main/java/com/djccnt15/northwind/domain/purchase/business/PurchaseOrderBusiness.java`
  - `createPurchaseOrder()` — 동일 패턴 적용
- `src/main/java/com/djccnt15/northwind/domain/stocktake/business/StockTakeBusiness.java`
  - `saveStockTakes()` — product 조회만 배치화. upsert 루프는 건별 유지 (`saveItem` private 메서드 인라인)

## 신규 메서드 시그니처

### ProductService.getProducts

```java
public Map<Long, ProductEntity> getProducts(List<Long> ids)
```

- `repository.findAllById(ids)`로 1회 조회 후 `Map<Long, ProductEntity>`로 변환
  (`Collectors.toMap(ProductEntity::getId, Function.identity())`)
- 요청한 ID(distinct 기준) 중 조회 결과에 없는 것이 있으면 `ApiException(NOT_FOUND, "error.product.notFound")` 발생
  — 기존 `getProduct(Long)`의 NOT_FOUND 동작 유지
- `@EntityGraph` 미사용 — 생성 컨텍스트에서 `productCategory` 접근이 없어 단순 `findAllById`로 충분
  (`OrderDetailConverter.toEntity()`/`PurchaseOrderDetailConverter.toEntity()`는 `getUnitPrice()`,
  `getStandardUnitCost()` 등 직접 필드만 사용)

## 변경 패턴 (Before → After)

```java
// Before: 루프 내 개별 쿼리 (N번)
request.getOrderDetails().forEach(detailReq -> {
    var product = productService.getProduct(detailReq.getProductId());
    ...
});

// After: 1회 배치 조회 + 메모리 매핑
var productIds = request.getOrderDetails().stream()
    .map(OrderDetailCreateReq::getProductId).toList();
var productMap = productService.getProducts(productIds);
request.getOrderDetails().forEach(detailReq -> {
    var product = productMap.get(detailReq.getProductId());
    ...
});
```

PurchaseOrderBusiness는 `PurchaseOrderDetailCreateReq::getProductId`, StockTakeBusiness는
`StockTakeItemReq::getProductId`로 동일 패턴 적용.

## API 엔드포인트

변경 없음. 아래 엔드포인트들의 내부 구현만 개선되었으며 요청/응답 형식은 그대로다.

| 메서드 | 경로 | 인증 | 비고 |
|--------|------|------|------|
| POST | /api/v1/orders | 필요 | 주문 생성 — 내부 product 조회 배치화 |
| POST | /api/v1/purchase-orders | 필요 | 구매주문 생성 — 내부 product 조회 배치화 |
| POST | /api/v1/stock-takes | 필요 | 재고실사 저장 — 내부 product 조회만 배치화, upsert는 건별 유지 |

(경로는 기존 컨트롤러 매핑을 따른다. 본 작업은 경로/바디를 변경하지 않음.)

## 응답 타입 정의

변경 없음. 기존 `OrderRes`, `PurchaseOrderRes`, `List<StockTakeRowRes>` 응답 형식 그대로 유지.

## 검증 결과

- 영향 도메인 테스트 전체 통과 (H2 프로파일):
  `.\gradlew.bat test --tests "...domain.order.*" --tests "...domain.purchase.*" --tests "...domain.stocktake.*" --tests "...domain.product.*"` → BUILD SUCCESSFUL
  - `OrderBusinessTest`, `PurchaseOrderBusinessTest`, `StockTakeBusinessTest`(upsert/배치 시나리오 포함),
    각 Service/Controller 테스트 모두 통과
- 전체 `.\gradlew.bat test` 실행 시 15건 실패는 모두 `@DevTest`(`sampledata` 시드 생성) 테스트로,
  `@ActiveProfiles("dev")`에 의해 실 MySQL(localhost:3306) 연결을 요구한다. 현재 환경에 MySQL이 없어
  `CommunicationsException`/`ConnectException`으로 초기화 단계에서 실패한 것이며 본 리팩토링과 무관하다
  (compileJava/compileTestJava 정상, 코드 로직 실패 아님).

## QA 중점 확인 항목 (계획서 기준)

- [x] 배치 조회 시 존재하지 않는 productId → NOT_FOUND 예외 발생 (distinct 개수 비교로 누락 감지)
- [x] API 계약 변경 없음 — 기존 테스트로 검증 충분, 모두 통과
- [x] Business 레이어 루프 내 개별 `getProduct()` 호출 완전 제거 (3개 메서드 모두)
