# 재고 관리 / 주문 처리 Race Condition 검토 리포트

## 개요

재고 관리(inventory) 및 주문 처리(order processing) 로직에서 race condition이 발생할 수 있는 부분을 검토한 리포트. 코드 변경 없는 리뷰 결과이며, 조사는 order 도메인, product/inventory 도메인, 프로젝트 전반의 동시성 제어 컨벤션(`@Version`, `@Lock`, 트랜잭션 설정)을 대상으로 함.

**전제 정정**: 이 코드베이스에는 아직 "주문 시 재고 차감" 로직 자체가 존재하지 않는다. `ProductEntity`에는 `unitsInStock` 같은 실시간 재고 필드가 없고, 재고는 `StockTakeEntity`(주기적 실사 스냅샷)로만 관리되며 Order 도메인과 연결되어 있지 않다. 따라서 "재고 차감 시 lost update" 같은 전형적인 시나리오는 존재하지 않는 코드 경로이다. 대신 실제로 발견된 race condition 후보는 아래 2건이다.

---

## 발견 1 (High): 주문 상태 전이(status transition)의 read-validate-write 레이스

**위치**: `src/main/java/com/djccnt15/northwind/domain/order/service/OrderService.java:84-123` (`updateOrderStatus`, `validateStatusTransition`), 동일 패턴이 `OrderDetailService.updateOrderDetailStatus`에도 적용됨. 호출부: `OrderBusiness.updateOrderStatus` / `updateOrderDetailStatus` (`domain/order/business/OrderBusiness.java:117-131`), 둘 다 `@Transactional(rollbackFor = Exception.class)`만 붙어있고 격리 수준/락 지정 없음.

**패턴**:
```java
public void updateOrderStatus(OrdersEntity entity, OrderStatusEntity newStatus) {
    validateStatusTransition(entity.getOrderStatus(), newStatus);  // 현재 상태를 읽어 검증
    ...
    entity.setOrderStatus(newStatus);
    repository.save(entity);  // 검증 통과 후 그대로 저장
}
```
- `OrdersEntity`/`OrderDetailEntity`에는 `@Version` 필드가 없음 (반면 `StockTakeEntity`는 이미 `@Version` 사용 — 팀이 패턴을 알고 있으면서도 여기엔 적용 안 함).
- 조회 메서드(`OrdersRepo.findWithDetailById`, `OrderDetailRepo.findWithRelationById`)에도 `@Lock(LockModeType...)` 없음, 프로젝트 전체에 `SELECT ... FOR UPDATE` 사용 사례 전무.
- 기본 격리 수준(MySQL 기본 `READ_COMMITTED`), 기본 propagation.

**레이스 시나리오**: 동일 주문에 대해 두 요청이 거의 동시에 상태 변경을 시도하면(예: 관리자 중복 클릭, 또는 취소 요청과 배송 처리 요청이 겹치는 경우) 둘 다 같은 "이전 상태"를 읽고 각자 `validateStatusTransition`을 통과한 뒤 순차적으로 커밋된다. `validateStatusTransition`은 순방향 전진만 허용하고 터미널 상태(DELIVERED/CANCELLED)에서의 전이를 막지만, 이 검증은 커밋 전 stale 데이터 기준으로 이루어지므로 두 트랜잭션 모두 "유효한 전이"로 통과할 수 있다. 결과적으로 나중에 커밋된 쪽이 조용히 이전 결과를 덮어쓰며, 어느 쪽도 에러를 받지 못한 채 비즈니스 규칙(예: "배송된 주문은 취소 불가")이 실질적으로 깨질 수 있다.

**영향**: 주문 상태 무결성 위반, 오류 없이 조용히 발생하는 lost update — 배송/정산 등 후속 프로세스가 잘못된 상태를 기준으로 동작할 위험.

---

## 발견 2 (Medium): StockTake 신규 레코드 생성의 check-then-insert 레이스

**위치**: `src/main/java/com/djccnt15/northwind/domain/stocktake/service/StockTakeService.java:65-85` (`upsert`)

**패턴**:
```java
var existing = repository.findByProductAndStockTakeDate(product, date);
if (existing.isPresent()) {
    existing.get().setQuantityOnHand(quantityOnHand);
    repository.saveAndFlush(existing.get());  // @Version으로 보호됨 (OK)
} else {
    // 신규 생성 분기 — 존재 확인과 삽입 사이에 유니크 제약이나 락이 전혀 없음
    ...
    repository.save(newEntity);
}
```
- 기존 레코드 업데이트 분기는 `StockTakeEntity.version`(`@Version`) 덕분에 `OptimisticLockingFailureException` → 400으로 안전하게 처리됨(이미 잘 만들어진 부분).
- 그러나 신규 생성 분기는 무방비: `(product_id, stock_take_date)` 조합에 DB 유니크 제약이 없음 — 엔티티 `@Table` 어노테이션에도, `doc/EDR.md`의 ERD 정의(`stock_take` 테이블)에도 유니크 키 표시가 없고 `ddl-auto: update`로도 추가되지 않음.
- 두 요청이 동일한 (product, date) 조합에 대해 "아직 없음"을 동시에 확인하면 둘 다 새 행을 INSERT하게 되어 같은 product+date에 중복 행이 생긴다. 이후 "최신 재고"를 구하는 로직(`findFirstByProductOrderByIdDesc`, `StockTakeRepo.findLatestByProductIds` — 둘 다 `MAX(id)` 기준)이 둘 중 나중에 삽입된(더 큰 id) 행을 임의로 "현재 재고"로 취급하며, 먼저 제출된 실사 수량은 조용히 무시된다.
- `StockTakeBusiness.saveStockTakes`가 한 트랜잭션 안에서 항목별로 `upsert`를 반복 호출하는 구조이므로, 동일 배치가 중복 제출되는 경우에도 같은 레이스가 항목 단위로 재현될 수 있다.

**영향**: 중복 실사 레코드로 인한 재고 수량 데이터 정합성 훼손 (조용한 데이터 손실, 예외 없음).

---

## 결론 요약

| # | 위치 | 유형 | 심각도 | 현재 보호 장치 |
|---|------|------|--------|---------------|
| 1 | `OrderService.updateOrderStatus` / `OrderDetailService.updateOrderDetailStatus` | read-validate-write 상태 전이 | High | 없음 |
| 2 | `StockTakeService.upsert` 신규 생성 분기 | check-then-insert | Medium | 없음 (업데이트 분기만 `@Version` 보호) |

참고: "주문 시 재고 차감" 관련 race condition은 해당 기능 자체가 아직 구현되어 있지 않아 현재는 존재하지 않음. 향후 이 기능을 추가할 경우, `ProductEntity`에 실시간 재고 필드가 생기는 시점에 반드시 낙관적 락(`@Version` + 재시도) 또는 원자적 조건부 UPDATE(`UPDATE product SET stock = stock - :qty WHERE id = :id AND stock >= :qty`, 영향받은 행 수로 재고 부족 판정) 중 하나를 함께 설계해야 한다.

## 권장 개선 방향 (참고용, 미구현)

- **발견 1**: `OrdersEntity`, `OrderDetailEntity`에 `@Version` 필드 추가 → 상태 전이 저장 시 낙관적 락 충돌을 `OptimisticLockingFailureException`으로 감지, `StockTakeService.upsert`와 동일하게 400으로 변환.
- **발견 2**: `stock_take` 테이블에 `(product_id, stock_take_date)` 유니크 제약 추가, 삽입 시 제약 위반 예외를 처리하는 로직 보강.
