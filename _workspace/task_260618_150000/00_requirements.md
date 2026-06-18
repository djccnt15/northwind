# 요구사항

## 기능 설명

N+1 쿼리 방지 리팩토링: 주문 생성, 구매주문 생성, 재고실사 저장 시 루프 내 개별 `productService.getProduct()` 호출을 배치 조회로 개선한다.

## 범위: 백엔드 전용

프론트엔드 변경 없음. 기존 API 계약(요청/응답 형식)은 변경되지 않는다.

## 위반 3건

1. **`OrderBusiness.createOrder()`** — `request.getOrderDetails().forEach`에서 `productService.getProduct(detailReq.getProductId())`를 N번 호출
2. **`PurchaseOrderBusiness.createPurchaseOrder()`** — `request.getPurchaseOrderDetails().forEach`에서 동일 패턴
3. **`StockTakeBusiness.saveStockTakes()`** — `request.getItems().stream().map(saveItem)`에서 `productService.getProduct(item.getProductId())`를 N번 호출

## 참고 도메인 (유사한 기존 구현)

- `StockTakeBusiness.getStockTakeRows()` — productIds 추출 → 배치 조회 → Map 매핑 패턴의 모범 사례
- `OrderBusiness.getOrders()` — ids 추출 → `getTotalAmounts(ids)` 배치 집계 패턴

## 특이사항

- 생성(create)/저장(save) 트랜잭션 내부 루프이므로 upsert/save 자체는 건별 유지 — product 조회만 배치화
- `OrderDetailConverter.toEntity()`는 `product.getUnitPrice()`, `product.getStandardUnitCost()` 등 직접 필드만 사용 → `@EntityGraph(productCategory)` 불필요
- `PurchaseOrderDetailConverter.toEntity()`도 동일 — `product.getStandardUnitCost()`만 사용
- 배치 조회 시 요청한 ID 중 존재하지 않는 것이 있으면 NOT_FOUND 예외를 던져야 함 (기존 `getProduct()` 동작 유지)

## Worktree
- 브랜치: feature/fix-n-plus-one-product-query
- 경로: C:/projects/northwind/.worktree/feature/fix-n-plus-one-product-query
