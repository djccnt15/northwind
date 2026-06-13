# 요구사항

## 기능 설명

StoryBoard.md S-50/51/52 "발주 관리(구매)" 화면을 풀스택으로 구현한다.

- **S-50 발주 목록** (`/purchase-orders`): 전체 발주 현황 조회. 검색/상태 필터, 서버 사이드 페이지네이션.
- **S-51 발주 상세** (`/purchase-orders/:id`): 발주 헤더 정보, 발주 항목(품목) 목록, 상태 전이(승인/반려/수령/대금지급) 처리.
- **S-52 발주 생성** (`/purchase-orders/new`): 공급사(Vendor) 선택 + 발주 항목(상품/수량/단가) 입력 후 등록.

접근 권한: `ADMIN, PURCHASE` (`RoleConst.PURCHASE`는 이미 정의되어 있음).

## 범위: 풀스택

## 참고 도메인 (유사한 기존 구현)

**S-40/41/42 주문 관리** (`feature/order-management`, PR #18, 커밋 08b4d3b)가 구조적으로 가장 유사한 선행 구현이다. 발주(purchase order)는 주문(order)과 "헤더 + 라인아이템 + 상태 전이 워크플로우 + 거래처/상품 룩업" 구조가 동일하다.

- 백엔드: `src/main/java/com/djccnt15/northwind/domain/order/**` 전체 (entity/converter/service/business/controller/validation)
  - 엔티티: `db/entity/OrdersEntity.java`, `OrderDetailEntity.java`, `OrderStatusEntity.java`, `OrderDetailStatusEntity.java`
  - 리포지토리: `db/repository/OrdersRepo.java` (`findByFilter`, `findWithDetailById`, `findTotalAmountByIdIn` 프로젝션), `OrderDetailRepo.java`
- 프론트엔드: `frontend/src/pages/orders.tsx`, `order-detail.tsx`, `order-new.tsx`, `frontend/src/entities/order.ts`
- 라우트 가드: `frontend/src/app/provider/redirect-route.tsx`의 `OrderRoute` (동일 패턴으로 `PurchaseRoute` 추가)

## 특이사항 (ERD 보완 필요 — 설계 방향 확정됨)

ERD(`doc/EDR.md`)의 `purchase_orders`/`purchase_order_status` 테이블과 엔티티(`db/entity/PurchaseOrderEntity.java`, `PurchaseOrderStatusEntity.java`)는 이미 존재하지만, 다음 두 가지가 ERD에 빠져 있어 `order` 도메인 패턴을 그대로 따르려면 보완이 필요하다. 아래는 오케스트레이터가 대화 맥락(이전 계획 논의)을 바탕으로 확정한 설계이며, `order_detail`/`order_status`의 기존 패턴을 그대로 미러링하는 수준이라 별도 ad-hoc 설계 합의 없이 진행한다.

1. **발주 항목(라인아이템) 테이블 부재**: `order_detail`에 대응하는 `purchase_order_detail` 테이블이 ERD에 없다. S-51/52 화면에 "상품명/단가/발주수량/소계" 목록이 필요하므로, `order_detail`을 미러링한 `purchase_order_detail` 엔티티를 신설한다.
   - 컬럼: `id`, `quantity`(int), `unit_price`(decimal, 발주 시점의 공급가/원가), `product_id`(FK), `purchase_order_id`(FK) + BaseEntity audit 컬럼
   - `order_detail`과 달리 **품목별 상태(`order_detail_status`에 대응하는 것)는 두지 않는다** — 발주는 PO 헤더 단위로만 상태를 관리한다(아래 2번). `discount` 컬럼도 두지 않는다(발주서에 할인율 개념 없음, StoryBoard S-51 mockup에도 없음).
2. **상태 전이 식별을 위한 `code` 컬럼 부재**: `purchase_order_status`는 ERD상 `name`/`sort_order`만 있어 `OrderStatusEntity`처럼 `code` 기반 전이 검증(`OrderService.validateStatusTransition`)을 그대로 적용할 수 없다. `order_status`를 미러링해 `code` 컬럼(unique)을 추가한다.
   - 상태값(코드 / 한글명, sortOrder=ASC로 시딩):
     - `DRAFT` / 작성중
     - `PENDING_APPROVAL` / 승인대기
     - `APPROVED` / 승인완료
     - `RECEIVED` / 수령완료
     - `PAID` / 대금지급완료
     - `REJECTED` / 반려
   - 전이 규칙: `DRAFT → PENDING_APPROVAL → APPROVED → RECEIVED → PAID` 순방향만 허용(역행 불가, 동일 상태 재설정 불가). `REJECTED`는 `DRAFT` 또는 `PENDING_APPROVAL`에서만 도달 가능하고 도달 시 종료 상태(이후 전이 불가) — `order`의 `CANCELLED` 처리 로직을 참고.
   - 상태 전이 시 날짜 기록: `APPROVED` → `approvedDate` 자동 기록(이미 entity에 필드 존재), `RECEIVED` → `receivedDate` 자동 기록(이미 존재), `PAID` → `paymentDate`/`paymentAmount`/`paymentMethod`는 별도 요청 바디로 받아 함께 기록(주문 합계 자동 계산 가능하면 `paymentAmount` 기본값으로 사용 가능).
   - `approvedBy`/`submittedBy`는 `EmployeeEntity` 참조(`domain/user/service/EmployeeService.getEmployee(AppUserEntity)`로 현재 로그인 사용자의 직원 레코드 조회). 별도 "승인권자" 역할은 두지 않고 `ADMIN`/`PURCHASE` 권한 보유자가 제출·승인 모두 수행 가능(주문 도메인과 동일하게 단일 권한으로 전체 워크플로우 처리).

3. **S-51 mockup의 "행별 [수령 처리]" 버튼**: ERD/엔티티상 품목별 수령 상태 컬럼이 없으므로, "수령 처리"는 PO 헤더 상태를 `RECEIVED`로 전이하는 단일 액션으로 구현한다(품목별 버튼이 아닌 헤더 영역의 상태 전이 버튼/드롭다운). UI 레이아웃은 와이어프레임을 그대로 따르지 않아도 된다.

4. **발주 항목 단가(`unit_price`)의 기준값**: 상품의 `standard_unit_cost`(원가)를 기본값으로 사용한다(판매가 `unit_price`가 아님 — 발주는 매입이므로). `domain/product` 도메인의 `ProductEntity.standardUnitCost` 참고.

## 시딩 필요 데이터 (테스트)

`src/test/resources/data-h2.sql`에 `purchase_order_status` 6건(위 코드/이름) 시드 추가, 발주 샘플 데이터(거래처 id 2 = Fast Shipping Inc, company_type_id=2 Supplier를 vendor로 사용 가능) 추가 필요.

## Worktree
- 브랜치: feature/purchase-order-management
- 경로: C:/projects/northwind/.worktree/feature/purchase-order-management
