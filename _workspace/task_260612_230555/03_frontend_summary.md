# 프론트엔드 구현 요약 — 발주 관리 (Purchase Order, S-50/51/52)

`domain/order` 프론트(`orders.tsx`/`order-detail.tsx`/`order-new.tsx`)를 미러링해 발주 관리 3개 화면을 구현했다.
`02_backend_contract.md`의 API 계약과 `~Ifs` 필드를 1:1로 맞췄으며, FSD 레이어 의존 규칙(`app → pages → widgets → features → entities → shared`)을 준수했다.

빌드 검증: `cd frontend; npm run build` (`tsc -b && vite build`) 통과.
- 주의: feature worktree에 `node_modules`가 없어 `npm ci`로 의존성을 먼저 설치한 뒤 빌드했다.
- 출력 경고는 모두 라이브러리/번들 크기 관련(pre-existing)이며 구현 코드와 무관하다.

## 생성/수정된 파일

### 생성 (신규)
- `frontend/src/entities/purchase-order.ts` — 타입 정의 (이전 세션에서 작성)
- `frontend/src/pages/purchase-orders.tsx` — S-50 발주 목록 (이전 세션에서 작성)
- `frontend/src/pages/purchase-order-detail.tsx` — S-51 발주 상세
- `frontend/src/pages/purchase-order-new.tsx` — S-52 발주 생성

### 수정
- `frontend/src/entities/index.ts` — purchase-order 타입 public export 추가
- `frontend/src/app/provider/redirect-route.tsx` — `PurchaseRoute` 가드 추가 (ADMIN 또는 PURCHASE)
- `frontend/src/app/router.tsx` — `PurchaseRoute` 라우트 그룹 + 페이지 import 추가
- `frontend/src/widgets/navbar-left.tsx` — "Purchase Orders" 메뉴 추가 (ADMIN 또는 PURCHASE 권한 시 노출)

## 추가된 라우트

| 경로 | 페이지 컴포넌트 | 인증 |
|------|----------------|------|
| `/purchase-orders` | `PurchaseOrders` | PurchaseRoute (ADMIN \| PURCHASE) |
| `/purchase-orders/new` | `PurchaseOrderNew` | PurchaseRoute (ADMIN \| PURCHASE) |
| `/purchase-orders/:id` | `PurchaseOrderDetail` | PurchaseRoute (ADMIN \| PURCHASE) |

- 라우터에서 `/purchase-orders/new`를 `/purchase-orders/:id`보다 먼저 등록했다. (React Router 7은 정적 세그먼트를 동적보다 우선 랭크하므로 순서와 무관하게 안전하나, 가독성을 위해 명시적으로 앞에 배치)

## 주요 구현 사항

### S-51 발주 상세 (`purchase-order-detail.tsx`)
- 헤더: PO 번호, 상태 배지, 상태 전이 액션 버튼.
- 상태 전이 규칙을 프론트에서 미러링(`STATUS_RANK` + `TERMINAL_CODES` + `REJECTABLE_CODES`):
  - 순방향 1단계 전진 액션만 노출(`DRAFT→PENDING_APPROVAL→APPROVED→RECEIVED→PAID`).
  - `REJECTED`는 `DRAFT`/`PENDING_APPROVAL`에서만 노출(Reject 버튼, tomato-red).
  - `PAID`/`REJECTED`는 종료 상태 — 액션 없음.
- 액션은 `code` 기준으로 매핑 → `statuses` 목록에서 실제 `statusId`를 찾아 `PATCH /v1/purchase-orders/{id}/status` 호출. (status id 하드코딩 없음)
- `PAID` 전이 시 결제 정보 입력 모달(`ModalOverlay`/`ModalDefault`) 노출 — `paymentDate`/`paymentAmount`/`paymentMethod` 입력. `paymentAmount` 기본값은 `totalAmount`. `Escape`로 닫힘(`useKeyDown`).
- 발주 항목 테이블은 읽기 전용(Product/Unit Price/Qty/Subtotal). order_detail과 달리 discount/품목별 status 컬럼 없음.
- 헤더 정보: vendor, status, 각 날짜, submittedBy/approvedBy(이름 조합), shippingFee/taxAmount/paymentAmount/paymentMethod/note 표시.

### S-52 발주 생성 (`purchase-order-new.tsx`)
- 공급사 선택: `GET /v1/purchase-orders/company-types`로 Supplier 타입을 찾아 `GET /v1/purchase-orders/companies?type={supplierTypeId}`로 vendor 옵션 로드. (`isSupplierType`로 "supplier"/"vendor" 라벨 매칭)
- 상품 검색: `productKeyword` 400ms 디바운스 → `GET /v1/purchase-orders/products?keyword=`로 `ProductCostOptionIfs` 로드.
- 상품 선택 시 `standardUnitCost`(원가)를 단가 기본값으로 자동입력하되, 사용자가 단가를 수정 가능(Unit Cost 입력 필드). 수정값을 `unitPrice`로 전송.
- 소계/합계 계산은 `unitPrice * quantity` (HALF_UP 2자리 반올림으로 백엔드 BigDecimal 정합) + shippingFee. **판매가가 아닌 원가 기준.**
- 제출: `POST /v1/purchase-orders` — `vendorId`/`shippingFee`/`taxAmount`/`note`/`purchaseOrderDetails[{productId,quantity,unitPrice}]`. 1400(검증 오류) 시 `body` 메시지 합쳐 alert. 성공 시 생성된 상세로 이동.

## 백엔드 계약과의 정합성 (경계면)
- `PurchaseOrderListItemIfs`/`PurchaseOrderIfs`/`PurchaseOrderDetailIfs`/`PurchaseOrderStatusIfs`/`PurchaseOrderStatusRefIfs`/`VendorOptionIfs`/`ProductCostOptionIfs` — 계약 응답 필드와 1:1 매칭(`entities/purchase-order.ts`).
- 페이지네이션: 백엔드 표준 Spring `Page<T>`를 `PageIfs<T>`로 받아 `body.content`/`body.page.totalElements` 사용(목록 페이지).
- 공급사 옵션은 `VendorOptionConverter`(백엔드 빈 충돌 회피용 명명)와 무관하게 응답 형태 `{id,name}`만 사용.

## QA 주의사항 / 의존 관계
- 권한 가드는 `PurchaseRoute`(ADMIN 또는 PURCHASE). 백엔드 컨트롤러 `@PreAuthorize("hasAnyAuthority('ADMIN','PURCHASE')")`와 일치.
- 상태 전이 UI는 "프론트 노출 가이드"일 뿐이며, 최종 검증은 서버가 수행한다(역행/스킵/종료상태 위반 시 400 `invalidStatusTransition` → alert로 표시).
- 직원 미연결 사용자가 발주 생성 시 백엔드 400(`employeeRequired`) → 생성 페이지 alert로 메시지 노출됨.
- `taxAmount`는 생성 요청에 포함하나 합계 계산에는 미반영(백엔드가 totalAmount = 항목 subtotal 합 + shippingFee로 산정). 표시상의 합계와 백엔드 totalAmount가 일치하도록 프론트도 동일 공식 사용.
- feature worktree에는 `node_modules`가 새로 설치되었다(빌드 검증용). 커밋 시 `.gitignore` 대상이므로 추적되지 않는다.
