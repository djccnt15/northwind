# 구현 계획

## 백엔드 작업 항목

`domain/order/**`를 1:1로 대응시켜 `domain/purchase/**`에 구현한다 (패키지명은 이미 `purchase`로 typo 수정 완료).

### 1. Entity / Repository

- **`PurchaseOrderStatusEntity`** (기존 파일 `db/entity/PurchaseOrderStatusEntity.java` 수정): `OrderStatusEntity`처럼 `code`(unique, `PurchaseOrderModelConst.CODE_MAX_LENGTH`) 컬럼 추가.
- **신규 `PurchaseOrderDetailEntity`** (`db/entity/PurchaseOrderDetailEntity.java`): `OrderDetailEntity` 미러링. 필드: `quantity`(Integer), `unitPrice`(BigDecimal), `product`(ManyToOne ProductEntity), `purchaseOrder`(ManyToOne PurchaseOrderEntity). `discount`/상태 필드는 두지 않음.
- **`PurchaseOrderEntity`** (기존 파일 수정): `@OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true) Set<PurchaseOrderDetailEntity> purchaseOrderDetails` 추가 (OrdersEntity의 `orderDetails` 패턴).
- **`PurchaseOrderStatusRepo`** (기존 파일 수정): `OrderStatusRepo` 패턴 참고 — `findAllByOrderByIdAsc()`, `findFirstByOrderByIdAsc()` 추가.
- **신규 `PurchaseOrderDetailRepo`** (`db/repository/PurchaseOrderDetailRepo.java`): `OrderDetailRepo` 패턴 참고 — `findWithRelationById` 등 필요한 메서드.
- **`PurchaseOrderRepo`** (기존 파일 수정): `OrdersRepo` 패턴 참고 —
  - 목록 검색용 `findByFilter(키워드, statusId, dateFrom, dateTo, pageable)` (검색 대상: 공급사명, 발주 상태 등 — `OrdersRepo.findByFilter` 쿼리 구조 참고)
  - 상세 조회용 `findWithDetailById(id)` (`@EntityGraph` 또는 JOIN FETCH로 vendor/status/submittedBy/approvedBy/purchaseOrderDetails 함께 로드)
  - 합계 프로젝션: `OrderTotalProjection`/`findTotalAmountByIdIn` 대응 — `PurchaseOrderTotalProjection` + `findTotalAmountByIdIn` (purchaseOrderDetails의 quantity * unitPrice 합)
  - 기존 `findByVendorIdOrderBySubmittedDateDesc`는 유지(거래처 상세 페이지 S-31의 발주 이력 표시에 사용 중인지 확인 — 사용 중이면 시그니처 유지)

### 2. Validation (`domain/purchase/validation/`)

- **`PurchaseOrderModelConst.java`** (기존 파일 수정): 기존 `NAME_MAX_LENGTH`(상태명) 유지 + `CODE_MAX_LENGTH`(상태 코드, `OrderStatusModelConst.CODE_MAX_LENGTH`와 동일하게 20) 추가. 발주 생성 요청(`PurchaseOrderCreateReq`)의 `*_NOT_NULL_MSG`/`*_NOT_EMPTY_MSG` 등 메시지 키 상수 추가 (`OrderModelConst` 참고).
- **신규 `PurchaseOrderErrorConst.java`**: `OrderErrorConst` 미러링 — `NOT_FOUND_ERR_MSG`("error.purchaseOrder.notFound"), `STATUS_NOT_FOUND_ERR_MSG`, `INVALID_STATUS_TRANSITION_ERR_MSG`, `DETAIL_NOT_FOUND_ERR_MSG` 등. `messages.properties`/`messages_ko.properties`(또는 `errors.properties`/`errors_ko.properties` — 기존 order 도메인이 어느 파일에 키를 등록했는지 확인 후 동일 파일에 추가)에 메시지 등록.
- **신규 `PurchaseOrderDetailModelConst.java`** / `PurchaseOrderDetailErrorConst.java` (필요 시 — `OrderDetailModelConst`/`OrderDetailErrorConst` 참고).

### 3. Converter (`domain/purchase/converter/`)

- **`PurchaseOrderConverter`**: `toResponse()`(상세), `toListResponse()`(목록, 합계 포함), `toEntity()`(생성 요청 → 엔티티) — `OrderConverter` 미러링.
- **`PurchaseOrderDetailConverter`**: `OrderDetailConverter` 미러링 (discount 없음).
- **`PurchaseOrderStatusConverter`**: `OrderStatusConverter` 미러링 (code 포함).
- **`CompanyOptionConverter`/`ProductOptionConverter`**: order 도메인의 동명 컨버터를 재사용할 수 있는지 확인 — 동일 모델(`CompanyOptionRes`/`ProductOptionRes`)이면 `domain/order/**`의 것을 import해 재사용, 패키지 의존이 부적절하면 `domain/purchase/converter/`에 동일 클래스를 둔다 (QA에서 중복 여부 판단).
  - **단, 상품 옵션은 발주이므로 `ProductOptionRes`에 `unitPrice`(판매가) 대신/추가로 `standardUnitCost`(원가)가 필요할 수 있음** — S-52에서 상품 선택 시 발주 단가 자동입력은 원가 기준이어야 함. 기존 `order` 도메인의 `ProductOptionRes`를 그대로 쓸지, `PurchaseProductOptionRes`(원가 포함)를 신설할지 결정.

### 4. Service (`domain/purchase/service/`)

- **`PurchaseOrderStatusService`**: `OrderStatusService` 미러링 — `getPurchaseOrderStatuses()`, `getPurchaseOrderStatus(id)`, `getFirstPurchaseOrderStatus()`.
- **`PurchaseOrderDetailService`**: `OrderDetailService` 미러링하되 품목별 상태 변경은 없음 — `getPurchaseOrderDetail(poId, detailId)` 정도만 필요할 수 있음(품목별 액션이 없다면 불필요 — 02_backend_contract.md에 결정 기록).
- **`PurchaseOrderService`**: `OrderService` 미러링 —
  - `getPurchaseOrders(kw, statusId, dateFrom, dateTo, pageable)`
  - `getTotalAmounts(ids)`
  - `getPurchaseOrder(id)`
  - `createPurchaseOrder(entity)`
  - `updatePurchaseOrderStatus(entity, newStatus, employee, paymentReq)`: 상태 전이 검증(`code` 기반 rank map: DRAFT=1, PENDING_APPROVAL=2, APPROVED=3, RECEIVED=4, PAID=5, REJECTED는 DRAFT/PENDING_APPROVAL에서만 전이 가능하고 종료 상태) + 상태별 부가 처리(APPROVED→approvedDate+approvedBy, RECEIVED→receivedDate, PAID→paymentDate/paymentAmount/paymentMethod)
  - `validateStatusTransition(current, next)`

### 5. Business (`domain/purchase/business/PurchaseOrderBusiness.java`)

- `OrderBusiness` 미러링. cross-domain 룩업 재사용: `CompanyService`(거래처, vendor=Supplier 타입 필터), `CompanyTypeService`, `ProductService`, `EmployeeService`(현재 사용자 → EmployeeEntity, submittedBy/approvedBy).
- `createPurchaseOrder(request, userSession)`: vendor 조회 → `EmployeeService.getEmployee(appUser)`로 submittedBy 조회(없으면 `ApiException(BAD_REQUEST, ...)` — 직원 정보 없는 사용자는 발주 생성 불가) → 첫 상태(`DRAFT`) 조회 → 발주 항목 각각 product 조회 후 `PurchaseOrderDetailEntity` 생성(unitPrice 기본값 = `product.getStandardUnitCost()`, 단 요청에 unitPrice가 명시되면 그 값 사용) → 저장.
- `updatePurchaseOrderStatus(poId, request, userSession)`: 상태 변경 + (APPROVED인 경우) 현재 사용자 EmployeeEntity를 approvedBy로 기록.

### 6. Controller (`domain/purchase/controller/PurchaseOrderApiController.java`)

`@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")`, `@RequestMapping(API_V1)` — `OrderApiController` 미러링:

- `GET /purchase-order-statuses` — 상태 목록
- `GET /purchase-orders/companies` — 공급사(Vendor=Supplier) 옵션 목록 (`CompanyTypeService`로 Supplier 타입 id 조회 후 필터)
- `GET /purchase-orders/products` — 상품 옵션 목록(원가 포함)
- `GET /purchase-orders` — 목록 (page, size, status, dateFrom, dateTo, keyword)
- `GET /purchase-orders/{id}` — 상세
- `POST /purchase-orders` — 생성
- `PATCH /purchase-orders/{id}/status` — 상태 전이 (body: `statusId` + PAID 전이 시 `paymentDate`/`paymentAmount`/`paymentMethod` optional)

### 7. Test

- `OrderApiControllerTest`/`OrderServiceTest` 등 기존 order 도메인 테스트 파일을 찾아 동일 커버리지로 `Purchase*Test` 작성 (목록/상세/생성/상태전이 happy path + 잘못된 상태 전이 400, 존재하지 않는 PO/상품/거래처 404).
- `src/test/resources/data-h2.sql`에 `purchase_order_status` 6건 시드 + 샘플 `purchase_orders`/`purchase_order_detail` 1건 추가 (00_requirements.md 참고).

---

## 프론트엔드 작업 항목

`frontend/src/pages/orders.tsx` / `order-detail.tsx` / `order-new.tsx` / `frontend/src/entities/order.ts`를 미러링.

### 1. entities (`frontend/src/entities/purchase-order.ts`)

`order.ts`의 인터페이스를 미러링 — `PurchaseOrderStatusIfs`(code, name, sortOrder), `VendorOptionIfs`, `ProductCostOptionIfs`(id, name, standardUnitCost), `PurchaseOrderDetailIfs`(id, product, unitPrice, quantity, subtotal — discount/status 없음), `PurchaseOrderListItemIfs`, `PurchaseOrderIfs`(submittedDate, approvedDate, receivedDate, paymentDate, paymentAmount, paymentMethod, shippingFee, taxAmount, note, vendor, submittedBy, approvedBy, status, purchaseOrderDetails, totalAmount).

→ 백엔드 `02_backend_contract.md`의 실제 응답 필드와 반드시 대조 후 확정 (필드명 불일치 없도록).

### 2. pages

- **`frontend/src/pages/purchase-orders.tsx`** (S-50): `orders.tsx` 미러링 — 검색/상태 필터/기간 필터, 서버사이드 페이지네이션 DataGrid, 행 클릭 → `/purchase-orders/:id`, "+ 신규 발주" → `/purchase-orders/new`.
- **`frontend/src/pages/purchase-order-detail.tsx`** (S-51): `order-detail.tsx` 미러링 — 헤더 정보(공급사/제출일/승인자/승인일/예상수령일 등), 상태 표시 + 상태 전이 액션(승인/반려/수령처리/대금지급완료 처리 — 현재 상태에 따라 가능한 액션만 노출), 발주 항목 테이블(상품명/단가/수량/소계, 읽기 전용), 합계.
- **`frontend/src/pages/purchase-order-new.tsx`** (S-52): `order-new.tsx` 미러링 — 공급사 선택(검색), 발주 항목 추가(상품 검색 → 단가 자동입력(원가)/수량 입력), 합계 표시, 등록.

### 3. router / navbar / route guard

- **`frontend/src/app/provider/redirect-route.tsx`**: `OrderRoute` 미러링한 `PurchaseRoute` 추가 (`ADMIN` 또는 `PURCHASE`).
- **`frontend/src/app/router.tsx`**: `OrderRoute` 그룹과 동일한 구조로 `PurchaseRoute` 그룹 추가 — `/purchase-orders`, `/purchase-orders/new`, `/purchase-orders/:id`.
  - 주의: `:id` 라우트보다 `/new`가 먼저 매칭되도록 order 라우트와 동일한 순서 유지.
- **`frontend/src/widgets/navbar-left.tsx`**: "Orders" 메뉴 항목과 동일한 패턴으로 `ADMIN` 또는 `PURCHASE` 권한일 때 "Purchase Orders" 메뉴 추가 (아이콘은 적절한 것 선택 — 기존 SVG 세트 중 박스/트럭 계열 있으면 사용, 없으면 Orders와 동일 아이콘 재사용 가능).

### 4. frontend/CLAUDE.md

신규 라우트 가드(`PurchaseRoute`) 추가에 대한 문서화가 필요하면 (`ProductRoute`/`ManagerRoute` 추가 시 했던 것처럼) `frontend/CLAUDE.md`의 라우트 가드 표에 행 추가.

---

## 참고 패턴

- 백엔드: `domain/order/**` 전체 (entity/repo/converter/service/business/controller/validation), 특히 `OrderService.validateStatusTransition`의 rank-map 기반 순방향 전이 검증 로직.
- 프론트엔드: `frontend/src/pages/order-detail.tsx`의 상태 전이 액션 버튼 노출 로직(현재 상태별 가능 액션 분기), `order-new.tsx`의 상품 검색 → 단가 자동입력 → 항목 추가/삭제 UX.
- i18n: `*ModelConst`(검증 어노테이션 message) / `*ErrorConst`(`messageUtil.getMessage()` 키) 분리, `messages*.properties`/`errors*.properties`에 키 등록 — order 도메인이 어떤 파일에 등록했는지 먼저 확인하고 동일하게.

## doc/ 영향 범위

- **`doc/StoryBoard.md`**: S-50/51/52 상태를 🔲 → ✅로 갱신 (QA PASS 시).
- **`doc/EDR.md`**: `purchase_order_status`에 `code` 컬럼 추가, `purchase_order_detail` 테이블 신설(필드: id, quantity, unit_price, product_id FK, purchase_order_id FK + audit) — ERD 다이어그램(mermaid)과 관계 섹션에 반영. (00_requirements.md의 "특이사항" 참고)
- **`doc/PRD.md`**: 영향 없음 — 발주 라인아이템은 PRD 4번 "엄격한 참조 무결성" 원칙(order_detail과 동일 원칙)의 연장이라 신규 비즈니스 규칙 추가는 아님.

## QA 중점 검토 항목

1. **백엔드-프론트엔드 경계면**: `02_backend_contract.md`의 응답 필드(특히 `PurchaseOrderIfs`/`PurchaseOrderDetailIfs`/상태 코드 값)와 `frontend/src/entities/purchase-order.ts`의 필드명·타입이 정확히 일치하는지.
2. **상태 전이 검증**: `code` 기반 rank map이 REJECTED(DRAFT/PENDING_APPROVAL에서만 도달 가능, 종료 상태)까지 올바르게 처리하는지, order의 CANCELLED 로직과 일관된 패턴인지.
3. **i18n**: `*ModelConst`/`*ErrorConst` 분리, 메시지 키 하드코딩 금지, `messages_ko.properties` 등 등록 누락 여부.
4. **권한**: 컨트롤러 `@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")`, 프론트 `PurchaseRoute`/navbar 가드 일치 여부 (S-60/61/62 작업 시 발견된 ADMIN 누락 패턴 재발 방지 — `feature/frontend-permission-routes` 작업의 QA 지적 사항 참고).
5. **발주 단가 기준**: 상품 옵션/발주 항목의 단가가 `standard_unit_cost`(원가)를 사용하는지, `unit_price`(판매가)를 잘못 재사용하지 않았는지.
6. **N+1**: `PurchaseOrderRepo.findWithDetailById`가 vendor/status/submittedBy/approvedBy/purchaseOrderDetails(+product)를 `@EntityGraph`/JOIN FETCH로 함께 로드하는지.
7. **테스트**: `.\gradlew.bat test` 전체 통과, `cd frontend; npm run build` 통과.
