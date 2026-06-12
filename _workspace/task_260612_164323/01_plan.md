# 구현 계획

## 백엔드 작업 항목

패키지 루트: `src/main/java/com/djccnt15/northwind/domain/order/`

### 1. Repository 보강 (`db/repository/`)

- `OrdersRepo`
  - 기존 `findByCustomerIdOrderByOrderDateDesc` 유지
  - `findByFilter(kw, statusId, dateFrom, dateTo, pageable)`: `CompanyRepo.findByFilter` 패턴 참고, `JOIN FETCH o.customer JOIN FETCH o.orderStatus LEFT JOIN FETCH o.shipper`, `customer.name LIKE :kw`, `(:statusId IS NULL OR orderStatus.id = :statusId)`, `(:dateFrom IS NULL OR o.orderDate >= :dateFrom)`, `(:dateTo IS NULL OR o.orderDate <= :dateTo)` + `countQuery`
  - `findWithDetailById(id)`: `@EntityGraph(attributePaths = {"customer", "shipper", "taxStatus", "orderStatus", "appUser", "orderDetails", "orderDetails.product", "orderDetails.orderDetailStatus"})`
- `OrderDetailRepo`: `findWithRelationById(id)` — `@EntityGraph(attributePaths = {"product", "orderDetailStatus", "order"})` (항목 상태 변경 시 사용)
- `OrderStatusRepo`: `findAllByOrderBySortOrderAsc()` (lookup 정렬용)
- `OrderDetailStatusRepo`: `findAllByOrderBySortOrderAsc()`
- (참고) `TaxStatusRepo`, `CompanyRepo`, `ProductRepo`는 기존 메서드 재사용, 신규 메서드 불필요 — 단, `companyService.getCompanies(kw, typeId, pageable)`을 lookup용으로 호출할 때 `Pageable`은 큰 사이즈(예: `PageRequest.of(0, 100, Sort.by("name"))`)로 호출

### 2. Validation (`domain/order/validation/`)

- `OrderModelConst` (신규): `NOTES_MAX_LENGTH`, `PAYMENT_TYPE_MAX_LENGTH`, `CUSTOMER_NOT_NULL_MSG`, `TAX_STATUS_NOT_NULL_MSG` 등 — `CompanyModelConst` 패턴
- `OrderErrorConst` (신규): `NOT_FOUND_ERR_MSG`("error.order.notFound"), `INVALID_STATUS_TRANSITION_ERR_MSG` 등
- `OrderDetailModelConst` (신규): `QUANTITY_MIN`, `DISCOUNT_MIN/MAX`, `PRODUCT_NOT_NULL_MSG` 등
- `OrderDetailErrorConst` (신규): `NOT_FOUND_ERR_MSG`("error.orderDetail.notFound")
- 기존 `OrderStatusModelConst`/`OrderDetailStatusModelConst`는 그대로 유지(엔티티 컬럼 길이 제약용)
- `messages.properties`/`messages_ko.properties`, `errors.properties`/`errors_ko.properties`에 신규 키 추가 (i18n 컨벤션 — `*ErrorConst` 값과 1:1 매칭)

### 3. Model (DTO) (`domain/order/model/`)

- `OrderStatusRes` { id, code, name, sortOrder }
- `OrderDetailStatusRes` { id, name, sortOrder }
- `CompanyOptionRes` { id, name } — lookup 경량 응답
- `ProductOptionRes` { id, name, unitPrice } — lookup 경량 응답
- `OrderListRes` { id, orderDate, customerName, shipperName, status{id,code,name}, totalAmount } — S-40 목록
- `OrderDetailRes` { id, product{id,name}, unitPrice, quantity, discount, subtotal, status{id,name} }
- `OrderRes` { id, orderDate, requiredDate, shippedDate, paidDate, shippingFee, taxRate, paymentType, notes, customer{id,name}, shipper{id,name}|null, taxStatus{id,status}, status{id,code,name}, orderDetails: List<OrderDetailRes>, totalAmount } — S-41 상세 및 S-42 생성 응답
- `OrderDetailCreateReq` { productId(NotNull), quantity(NotNull, Min=1), discount(0~100, default 0) }
- `OrderCreateReq` { customerId(NotNull), shipperId(nullable), requiredDate(nullable), taxStatusId(NotNull), paymentType(nullable), shippingFee(nullable), notes(nullable), orderDetails: List<OrderDetailCreateReq>(NotEmpty) }
- `OrderStatusUpdateReq` { statusId(NotNull) } — 헤더 상태 변경
- `OrderDetailStatusUpdateReq` { statusId(NotNull) } — 항목 상태 변경

### 4. Converter (`domain/order/converter/`)

- `OrderStatusConverter`: `toResponse(OrderStatusEntity) -> OrderStatusRes`
- `OrderDetailStatusConverter`: `toResponse(OrderDetailStatusEntity) -> OrderDetailStatusRes`
- `CompanyOptionConverter`: `toResponse(CompanyEntity) -> CompanyOptionRes` (id, name만)
- `ProductOptionConverter`: `toResponse(ProductEntity) -> ProductOptionRes` (id, name, unitPrice만)
- `OrderConverter`:
  - `toListResponse(OrdersEntity) -> OrderListRes` (totalAmount은 `orderDetails` 합산 — N+1 방지 위해 목록 쿼리도 `orderDetails` fetch 필요 여부 검토. 항목 수가 적으므로 `@EntityGraph`에 `orderDetails` 포함 권장)
  - `toResponse(OrdersEntity) -> OrderRes` (totalAmount 계산 포함)
  - `toEntity(OrderCreateReq, customer, shipper, taxStatus, orderStatus, appUser) -> OrdersEntity`
- `OrderDetailConverter`:
  - `toResponse(OrderDetailEntity) -> OrderDetailRes` (subtotal 계산 포함)
  - `toEntity(OrderDetailCreateReq, product, orderDetailStatus, order) -> OrderDetailEntity` (unitPrice/standardUnitCost는 product에서 스냅샷 복사)

### 5. Service (`domain/order/service/`)

- `OrderStatusService`: `getOrderStatuses()`, `getOrderStatus(id)`, `getFirstOrderStatus()`(PENDING, sortOrder 최소값)
- `OrderDetailStatusService`: `getOrderDetailStatuses()`, `getOrderDetailStatus(id)`, `getFirstOrderDetailStatus()`(대기)
- `OrderService`:
  - `getOrders(statusId, dateFrom, dateTo, kw, pageable)`
  - `getOrder(id)` — `findWithDetailById`, NOT_FOUND 처리
  - `createOrder(request, customer, shipper, taxStatus, orderStatus, appUser)`
  - `updateOrderStatus(entity, newStatus)` — 상태 코드 기반으로 `paidDate`/`shippedDate` 자동 세팅
  - `validateStatusTransition(current, next)` — 역행 전이 등 차단 (취소는 예외)
- `OrderDetailService`:
  - `getOrderDetail(orderId, detailId)` — `order.id` 일치 검증 후 NOT_FOUND
  - `updateOrderDetailStatus(entity, newStatus)`
- Lookup용 서비스는 신규 생성하지 않고 cross-domain 주입:
  - `CompanyService`(`domain/company`), `CompanyTypeService`(`domain/company`), `ProductService`(`domain/product`), `TaxStatusService`(`domain/tax`) — 모두 `OrderBusiness`에 직접 주입

### 6. Business (`domain/order/business/OrderBusiness.java`)

`@Business` + `@Transactional(rollbackFor = Exception.class)` (CompanyBusiness 패턴):

- `getOrders(...)`, `getOrder(id)`
- `createOrder(request, userSession)`:
  1. `customer = companyService.getCompany(request.getCustomerId())`
  2. `shipper = request.getShipperId() != null ? companyService.getCompany(...) : null`
  3. `taxStatus = taxStatusService.getTaxStatus(request.getTaxStatusId())`
  4. `orderStatus = orderStatusService.getFirstOrderStatus()`
  5. 각 `OrderDetailCreateReq`에 대해 `product = productService.getProduct(productId)`, `orderDetailStatus = orderDetailStatusService.getFirstOrderDetailStatus()`
  6. `appUser`는 `UserSession`에서 조회 (`AppUserRepo` 또는 기존 헬퍼 패턴 확인)
- `updateOrderStatus(orderId, request)`
- `updateOrderDetailStatus(orderId, detailId, request)`
- lookup 메서드: `getOrderStatuses()`, `getOrderDetailStatuses()`, `getCompanyTypes()`, `getCompanyOptions(typeId, keyword)`, `getProductOptions(keyword)`, `getTaxStatuses()`

### 7. Controller (`domain/order/controller/OrderApiController.java`)

`@RequestMapping(API_V1)` + `@PreAuthorize("hasAnyAuthority('ADMIN', 'ORDER')")`:

| Method | Path | 설명 |
|--------|------|------|
| GET | `/order-statuses` | 주문 상태 목록 |
| GET | `/order-detail-statuses` | 주문 항목 상태 목록 |
| GET | `/orders/company-types` | 거래처 유형 lookup |
| GET | `/orders/companies?type=&keyword=` | 거래처 lookup (고객사/배송사 선택용) |
| GET | `/orders/products?keyword=` | 상품 lookup (주문 항목 선택용) |
| GET | `/orders/tax-statuses` | 세금유형 lookup |
| GET | `/orders?page=&size=&status=&dateFrom=&dateTo=&keyword=` | S-40 목록 |
| GET | `/orders/{id}` | S-41 상세 |
| POST | `/orders` | S-42 생성 (201) |
| PATCH | `/orders/{id}/status` | 헤더 상태 변경 |
| PATCH | `/orders/{id}/details/{detailId}/status` | 항목 상태 변경 |

### 8. 시드 데이터

- `src/test/resources/data-h2.sql`에 `order_status`(5건), `order_detail_status`(3건), `tax_status`(최소 2건: 과세/면세) INSERT 추가 — `created_by`/`last_modified_by`는 기존 INSERT처럼 `1`(system) 사용
- 개발 MySQL DB 시드: 기존 `DataLoader`(`ApplicationReadyEvent`) 확장 여부는 백엔드 에이전트가 판단. 확장 시 idempotent하게(이미 존재하면 skip) 작성

### 9. 테스트

- `OrderServiceTest`/`OrderBusinessTest` (`@SpringBootTest` + `@Transactional`): 목록 필터, 상세 조회, 생성, 상태 전이(정상/역행 차단), 항목 상태 변경
- `OrderApiControllerTest` (`@WebMvcTest` 또는 통합 테스트): 권한 검증(`ADMIN`/`ORDER` 외 403), 응답 형식(`Api<T>` 래퍼) 검증
- 기존 `CompanyServiceTest` 등에서 `OrdersEntity`/`findByCustomerIdOrderByOrderDateDesc` 관련 테스트가 영향받지 않는지 확인

---

## 프론트엔드 작업 항목

### 1. entities (`frontend/src/entities/order.ts` 신규)

```typescript
export interface OrderStatusIfs { id: number; code: string; name: string; sortOrder: string | null; }
export interface OrderDetailStatusIfs { id: number; name: string; sortOrder: string | null; }
export interface CompanyOptionIfs { id: number; name: string; }
export interface ProductOptionIfs { id: number; name: string; unitPrice: number; }
export interface OrderDetailIfs {
  id: number;
  product: { id: number; name: string };
  unitPrice: number;
  quantity: number;
  discount: number;
  subtotal: number;
  status: { id: number; name: string };
}
export interface OrderListItemIfs {
  id: number;
  orderDate: string;
  customerName: string;
  shipperName: string | null;
  status: { id: number; code: string; name: string };
  totalAmount: number;
}
export interface OrderIfs {
  id: number;
  orderDate: string;
  requiredDate: string | null;
  shippedDate: string | null;
  paidDate: string | null;
  shippingFee: number | null;
  taxRate: number | null;
  paymentType: string | null;
  notes: string | null;
  customer: { id: number; name: string };
  shipper: { id: number; name: string } | null;
  taxStatus: { id: number; status: string };
  status: { id: number; code: string; name: string };
  orderDetails: OrderDetailIfs[];
  totalAmount: number;
}
```

`frontend/src/entities/index.ts`(public API)에 export 추가.

### 2. features (`frontend/src/features/order/` 신규, `features/company` 구조 참고)

- 주문 항목 추가/삭제 UI(상품 검색 dropdown + 수량/할인율 입력)는 `pages/order-new.tsx`에서 직접 구현해도 되나, 재사용성 위해 `OrderItemRow`/`ProductSearchSelect` 등을 `features/order/`로 분리 고려 — 페이지 복잡도에 따라 프론트 에이전트가 판단
- `features/order/index.ts` public API 작성

### 3. pages

- `pages/orders.tsx` (S-40): `pages/companies.tsx` 패턴
  - DataGrid 컬럼: 주문번호(id), 고객사(customerName, 클릭 시 `/orders/:id`), 주문일, 총액, 배송사, 상태
  - 상단: 검색(고객사명, 디바운스), 상태 탭(전체/접수/결제완료/출고/배송완료/취소 — `/order-statuses`로 동적 로드), 기간 필터(`requiredDate` input type="date" 2개)
  - `[+ 신규 주문]` → `/orders/new`
- `pages/order-detail.tsx` (S-41): `pages/company-detail.tsx` 패턴
  - 헤더: 주문번호, 상태 뱃지 + 상태 변경 `<select>`(다음 상태로만 활성화, 또는 취소)
  - 기본 정보 카드: 고객사/주문일/배송사/요청배송일/실제배송일/배송지(고객사 address — `customer`에 address 없으면 생략 가능, OrderRes에 address 미포함이므로 표시 안 함)
  - 주문 항목 테이블: 상품명/단가/수량/할인율/소계/항목상태(상태 변경 `<select>`)
  - 합계 표시
- `pages/order-new.tsx` (S-42)
  - 고객사 선택(`/orders/companies?type=<customerTypeId>`), 배송사 선택(`/orders/companies?type=<shipperTypeId>`) — `company-type` id는 `/orders/company-types`에서 이름으로 매칭(`companies.tsx`의 type 처리 참고)
  - 세금유형 선택(`/orders/tax-statuses`)
  - 요청배송일 date input
  - 주문 항목: `[+ 추가]`로 행 추가, 각 행에서 상품 검색(`/orders/products?keyword=`) → 선택 시 단가 자동입력(읽기전용 표시), 수량/할인율 입력, 행 삭제
  - 총액 실시간 계산(클라이언트 측 — `sum(unitPrice*qty*(1-discount/100)) + shippingFee`)
  - `[주문 등록]` → `POST /orders` → 성공 시 `/orders/:id`로 이동

### 4. 라우트 가드 (`app/provider/redirect-route.tsx`)

`ProductRoute` 패턴 그대로 `OrderRoute` 추가:

```typescript
export function OrderRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();
  if (!user || !(user.authorities.includes("ADMIN") || user.authorities.includes("ORDER"))) {
    return <Navigate to="/home" replace />;
  }
  return <>{children}</>;
}
```

### 5. router (`app/router.tsx`)

`ProductRoute` 블록과 동일한 형태로 추가 (⚠️ `/orders/new`는 `/orders/:id`보다 먼저 등록 — `products` 라우트 순서 참고):

```typescript
{
  path: "/",
  element: <OrderRoute><Layout /></OrderRoute>,
  children: [
    { path: "/orders", element: <Orders /> },
    { path: "/orders/new", element: <OrderNew /> },
    { path: "/orders/:id", element: <OrderDetail /> },
  ],
},
```

### 6. navbar (`widgets/navbar-left.tsx`)

`Companies`/`Products` 링크와 동일한 패턴으로 "Orders" 링크 추가 (`ADMIN` 또는 `ORDER` 권한 시 노출), 적절한 아이콘 사용.

### 7. `frontend/CLAUDE.md`

라우트 보호 표 (`ProtectedRoute`/`AdminRoute`/`ProductRoute`/`ManagerRoute`)에 `OrderRoute` 행 추가.

---

## 참고 패턴

- 페이지네이션/필터: `CompanyApiController.getCompanies` + `CompanyRepo.findByFilter` ↔ `Companies.tsx`
- 상세 조회 + 연관 데이터(주문/발주 이력): `CompanyApiController.getCompany/getOrders/getPurchaseOrders` ↔ `CompanyDetail.tsx`
- 도메인 간 서비스 재사용: `CompanyBusiness`가 `TaxStatusService`/`TaxStatusConverter`(`domain/tax`) 직접 주입 — `OrderBusiness`도 동일하게 `domain/company`, `domain/product`, `domain/tax` 서비스/컨버터 주입
- 상태 마스터 lookup 응답: `CompanyTypeRes`/`TaxStatusRes` 패턴을 `OrderStatusRes`/`OrderDetailStatusRes`/`CompanyOptionRes`/`ProductOptionRes`에 적용

## doc/ 영향 범위

- `doc/StoryBoard.md`:
  - S-40/S-41/S-42 상태 🔲 → ✅
  - "2. 전체 화면 흐름도"의 좌측 네비게이션 바 목록에 "주문" 항목은 이미 존재 — 변경 없음
  - "6. 미구현 화면 개발 우선순위" 표에서 S-40/41/42 행 제거 또는 "구현 완료" 주석 처리
- `doc/PRD.md`/`doc/EDR.md`: 영향 없음 (기존 ERD 구조 그대로 사용, 신규 테이블/컬럼 없음)

## QA 중점 검토 항목

1. **권한 경계**: `OrderApiController`에 추가한 lookup 엔드포인트(`/orders/companies`, `/orders/products`, `/orders/tax-statuses`, `/orders/company-types`)가 `ADMIN,ORDER` 권한만으로 정상 동작하고, `CompanyApiController`/`ProductApiController`의 기존 엔드포인트 권한(`ADMIN,COMPANY`/`ADMIN,PRODUCT`)에 영향을 주지 않는지 확인
2. **경계면 타입 일치**: `02_backend_contract.md`의 `OrderRes`/`OrderListRes`/`OrderDetailRes`/`*OptionRes` 필드와 `frontend/src/entities/order.ts`의 `~Ifs` 필드 1:1 대조
3. **상태 전이 검증**: 헤더 상태 역행/취소 후 재변경 등 잘못된 전이가 백엔드에서 차단되는지, 프론트 `<select>`가 비정상 값을 보내지 않는지
4. **금액 계산 일치**: 백엔드 `totalAmount`/`subtotal` 계산과 프론트 클라이언트 측 미리보기 계산이 동일한 공식을 쓰는지 (반올림/소수점 처리 포함)
5. **N+1**: 목록(`/orders`)에서 `orderDetails`까지 fetch하여 `totalAmount`를 계산할 경우 `@EntityGraph`/`JOIN FETCH` 적용 여부
6. **i18n**: `*ErrorConst` 분리, `messages*.properties`/`errors*.properties` 키 추가 여부
7. **프론트 빌드**: `cd frontend; npm run build` 통과 (S-30/31/32 작업 시 발생한 TS2367 류 오류 재발 방지)
