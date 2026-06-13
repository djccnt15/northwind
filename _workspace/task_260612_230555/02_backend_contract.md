# 백엔드 API 계약 — 발주 관리 (Purchase Order, S-50/51/52)

전체 백엔드 구현 완료. `.\gradlew.bat test -x buildFrontend` 전체 통과(160 tests, 0 fail).
`domain/order/**`를 미러링한 `domain/purchase/**`로 구현했으며, ERD 보완(상태 `code` 컬럼, `purchase_order_detail` 신규 테이블)을 반영했다.

## 생성/수정된 파일

### Entity (db/entity)
- `PurchaseOrderStatusEntity.java` (수정) — `code`(unique, max 20) 컬럼 추가, `name`/`sortOrder` 유지
- `PurchaseOrderDetailEntity.java` (신규) — `quantity`(Integer), `unitPrice`(BigDecimal), `product`(FK), `purchaseOrder`(FK). discount/품목상태 없음
- `PurchaseOrderEntity.java` (수정) — `Set<PurchaseOrderDetailEntity> purchaseOrderDetails` 추가 (cascade ALL, orphanRemoval)

### Repository (db/repository) + Projection (db/projection)
- `PurchaseOrderStatusRepo.java` (수정) — `findAllByOrderByIdAsc`, `findFirstByOrderByIdAsc`, `findByCode`
- `PurchaseOrderDetailRepo.java` (신규) — `findWithRelationById`
- `PurchaseOrderRepo.java` (수정) — `findByFilter`(vendor명 검색 + status/date 필터), `findWithDetailById`(EntityGraph: vendor/status/submittedBy/approvedBy/details+product), `findTotalAmountByIdIn`. 기존 `findByVendorIdOrderBySubmittedDateDesc` 유지(S-31에서 사용 중)
- `PurchaseOrderTotalProjection.java` (신규) — `getPurchaseOrderId()`, `getTotalAmount()`

### Validation (domain/purchase/validation)
- `PurchaseOrderStatusModelConst.java` (신규) — `CODE_MAX_LENGTH=20`, `NAME_MAX_LENGTH=100`
- `PurchaseOrderModelConst.java` (수정) — 생성/상태 요청 검증 메시지 키
- `PurchaseOrderDetailModelConst.java` (신규)
- `PurchaseOrderErrorConst.java` (신규), `PurchaseOrderDetailErrorConst.java` (신규)
- `messages.properties`/`messages_ko.properties`/`errors.properties`/`errors_ko.properties` (수정) — 키 등록

### Model (domain/purchase/model) — 응답/요청 DTO
- 응답: `PurchaseOrderRes`, `PurchaseOrderListRes`, `PurchaseOrderDetailRes`, `PurchaseOrderStatusRes`, `PurchaseOrderStatusRef`, `CompanyRef`, `ProductRef`, `EmployeeRef`, `CompanyOptionRes`, `ProductCostOptionRes`
- 요청: `PurchaseOrderCreateReq`, `PurchaseOrderDetailCreateReq`, `PurchaseOrderStatusUpdateReq`

### Converter (domain/purchase/converter)
- `PurchaseOrderConverter`, `PurchaseOrderDetailConverter`, `PurchaseOrderStatusConverter`, `VendorOptionConverter`, `ProductCostOptionConverter`
- 주의: order 도메인의 `CompanyOptionConverter`와 Spring 빈 이름 충돌을 피하려고 발주 쪽은 `VendorOptionConverter`로 명명(클래스명 = 빈 이름). 프론트와는 무관.

### Service / Business / Controller
- `PurchaseOrderService`, `PurchaseOrderStatusService` (domain/purchase/service)
- `PurchaseOrderBusiness` (domain/purchase/business) — `@Transactional` 경계, cross-domain(Company/CompanyType/Product/Employee) 조합
- `PurchaseOrderApiController` (domain/purchase/controller) — `@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")`

### Test
- `PurchaseOrderServiceTest`, `PurchaseOrderBusinessTest`(@SpringBootTest), `PurchaseOrderApiControllerTest`(@WebMvcTest)
- `src/test/resources/data-h2.sql` — purchase_order_status 6건, app_user `purchaser`(id 3, PURCHASE 권한) + 연결 employee, 샘플 purchase_orders/detail 1건

---

## API 엔드포인트

모든 경로는 `/api/v1` 프리픽스, 세션 인증 필요, 권한 `ADMIN` 또는 `PURCHASE`.
응답은 공통 `Api<T>` 래퍼(`{ serverTime, result:{code,message,description}, body }`)로 감싸진다. 아래 "응답 바디"는 `body` 내용.

| 메서드 | 경로 | 요청 | 응답 바디(body) |
|--------|------|------|----------------|
| GET | `/api/v1/purchase-order-statuses` | - | `PurchaseOrderStatusRes[]` |
| GET | `/api/v1/purchase-orders/company-types` | - | `CompanyTypeRes[]` (Supplier 타입 id 조회용) |
| GET | `/api/v1/purchase-orders/companies?type={typeId}&keyword={kw}` | query | `CompanyOptionRes[]` (공급사 옵션, type에 Supplier id 전달) |
| GET | `/api/v1/purchase-orders/products?keyword={kw}` | query | `ProductCostOptionRes[]` (원가 포함 상품 옵션) |
| GET | `/api/v1/purchase-orders?page&size&status&dateFrom&dateTo&keyword` | query | `Page<PurchaseOrderListRes>` |
| GET | `/api/v1/purchase-orders/{id}` | path | `PurchaseOrderRes` |
| POST | `/api/v1/purchase-orders` | `PurchaseOrderCreateReq` | `PurchaseOrderRes` (result.code=201) |
| PATCH | `/api/v1/purchase-orders/{id}/status` | `PurchaseOrderStatusUpdateReq` | `PurchaseOrderRes` |

### 목록 쿼리 파라미터 상세
- `page`(기본 0), `size`(기본 20) — 0-based 페이지
- `status` (선택, Long) — purchase_order_status id
- `dateFrom` / `dateTo` (선택, ISO `yyyy-MM-dd`) — submittedDate 범위
- `keyword` (기본 "") — 공급사명 LIKE 검색
- 정렬: submittedDate DESC 고정
- 응답 `Page<T>` 구조: 표준 Spring Page (`content`, `totalElements`, `totalPages`, `number`, `size`, ...). order 도메인 목록과 동일.

---

## 응답 타입 정의 (프론트엔드 `purchase-order.ts` 작성용)

### PurchaseOrderStatusRes
```
id: number
code: string          // 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'RECEIVED' | 'PAID' | 'REJECTED'
name: string          // 한글명 (작성중/승인대기/...)
sortOrder: string | null   // 'ASC' 등 (보통 미사용)
```

### PurchaseOrderStatusRef (목록/상세의 status 필드)
```
id: number
code: string
name: string
```

### CompanyRef / EmployeeRef / ProductRef
```
CompanyRef:  { id: number, name: string }
EmployeeRef: { id: number, firstName: string, lastName: string }
ProductRef:  { id: number, name: string }
```

### CompanyOptionRes (공급사 옵션)
```
id: number
name: string
```

### ProductCostOptionRes (상품 옵션, 발주 단가 자동입력용)
```
id: number
name: string
standardUnitCost: number   // BigDecimal → number. 발주 단가 기본값(원가). 판매가 아님!
```

### PurchaseOrderListRes (목록 행)
```
id: number
submittedDate: string          // ISO date 'yyyy-MM-dd'
vendorName: string | null
status: PurchaseOrderStatusRef
totalAmount: number            // BigDecimal → number. (항목 소계 합 + shippingFee)
```

### PurchaseOrderDetailRes (상세 항목 행, 읽기 전용)
```
id: number
product: ProductRef
unitPrice: number              // BigDecimal → number. 발주 단가
quantity: number
subtotal: number               // unitPrice * quantity (2 decimal)
```
※ order_detail과 달리 `discount`/품목별 `status` 없음.

### PurchaseOrderRes (상세)
```
id: number
submittedDate: string          // 'yyyy-MM-dd'
approvedDate: string | null    // APPROVED 전이 시 자동 기록
receivedDate: string | null    // RECEIVED 전이 시 자동 기록
paymentDate: string | null     // PAID 전이 시 기록
shippingFee: number | null     // Integer
taxAmount: number | null       // BigDecimal → number
paymentAmount: number | null   // Integer
paymentMethod: string | null
note: string | null
vendor: CompanyRef | null
submittedBy: EmployeeRef | null
approvedBy: EmployeeRef | null
status: PurchaseOrderStatusRef
purchaseOrderDetails: PurchaseOrderDetailRes[]
totalAmount: number            // 항목 subtotal 합 + shippingFee
```

---

## 요청 타입 정의

### PurchaseOrderCreateReq (POST /purchase-orders)
```
vendorId: number               // 필수 (공급사 company id)
shippingFee?: number | null    // Integer
taxAmount?: number | null      // BigDecimal
note?: string | null
purchaseOrderDetails: PurchaseOrderDetailCreateReq[]   // 필수, 최소 1개
```
- 생성 시 상태는 자동으로 `DRAFT`, `submittedDate`는 서버 today, `submittedBy`는 현재 로그인 사용자의 직원 레코드.
- **주의**: 로그인 사용자에 연결된 employee 레코드가 없으면 400(`error.purchaseOrder.employeeRequired`).

### PurchaseOrderDetailCreateReq
```
productId: number              // 필수
quantity: number               // 필수, 최소 1
unitPrice?: number | null      // 선택. 생략 시 서버가 product.standardUnitCost(원가)를 기본값으로 사용
```
- 프론트 S-52에서 상품 선택 시 `ProductCostOptionRes.standardUnitCost`를 단가 기본값으로 채우고, 그 값을 `unitPrice`로 전송하거나(또는 생략) 가능. 사용자가 단가를 수정할 수 있게 하려면 `unitPrice`를 명시 전송.

### PurchaseOrderStatusUpdateReq (PATCH /purchase-orders/{id}/status)
```
statusId: number               // 필수 (전이 대상 purchase_order_status id)
paymentDate?: string | null    // 'yyyy-MM-dd'. PAID 전이 시에만 의미. 생략 시 서버 today
paymentAmount?: number | null  // Integer. PAID 전이 시 기록
paymentMethod?: string | null  // PAID 전이 시 기록
```

---

## 상태 코드 / 워크플로우 (프론트 액션 버튼 분기 기준)

`purchase_order_status` 시드 (id ASC = 워크플로우 순서):

| id | code | name(한글) |
|----|------|-----------|
| 1 | DRAFT | 작성중 |
| 2 | PENDING_APPROVAL | 승인대기 |
| 3 | APPROVED | 승인완료 |
| 4 | RECEIVED | 수령완료 |
| 5 | PAID | 대금지급완료 |
| 6 | REJECTED | 반려 |

### 전이 규칙 (서버 검증)
- 순방향만 허용: `DRAFT → PENDING_APPROVAL → APPROVED → RECEIVED → PAID` (한 단계 이상 건너뛰는 전진은 허용, 역행/동일상태 불가).
- `REJECTED`: `DRAFT` 또는 `PENDING_APPROVAL`에서만 도달 가능. 도달 후 종료 상태(추가 전이 불가).
- `PAID`: 종료 상태(추가 전이 불가).
- 위반 시 400 `error.purchaseOrder.invalidStatusTransition`.

### 상태 전이 부가 효과 (자동)
- → `APPROVED`: `approvedDate` = today 자동, `approvedBy` = 현재 로그인 사용자 employee 자동.
- → `RECEIVED`: `receivedDate` = today 자동.
- → `PAID`: `paymentDate`/`paymentAmount`/`paymentMethod`를 요청 바디로 받아 기록(paymentDate 생략 시 today).

### 프론트 액션 버튼 권장 노출 (현재 status.code 기준)
- `DRAFT`: [승인요청→PENDING_APPROVAL] [반려→REJECTED]
- `PENDING_APPROVAL`: [승인→APPROVED] [반려→REJECTED]
- `APPROVED`: [수령처리→RECEIVED]
- `RECEIVED`: [대금지급완료→PAID] (paymentAmount/paymentMethod 입력 모달 권장)
- `PAID` / `REJECTED`: 액션 없음(종료)
- S-51 mockup의 "행별 수령 처리"는 헤더 단위 [수령처리] 단일 액션으로 구현(품목별 상태 없음).

## 에러 응답
- 404: `error.purchaseOrder.notFound`(PO 없음), `error.purchaseOrderStatus.notFound`(상태 id 없음), `error.product.notFound`, `error.company.notFound`
- 400: `error.purchaseOrder.invalidStatusTransition`, `error.purchaseOrder.employeeRequired`(직원 미연결 사용자)
- 1400: Bean Validation 실패 시 `body`에 `{ field: message }` Map (vendorId/purchaseOrderDetails/quantity 등)

## 권한 / 라우트
- 컨트롤러: `@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")`.
- 프론트: `PurchaseRoute`(ADMIN 또는 PURCHASE) 가드를 `OrderRoute` 미러링으로 추가.

## 단가 기준 (중요)
- 발주 단가는 **`standardUnitCost`(원가)** 기준. 판매가(`unitPrice`)가 아님. 상품 옵션 응답은 `ProductCostOptionRes.standardUnitCost`만 제공한다.
