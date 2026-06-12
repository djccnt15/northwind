# 프론트엔드 구현 요약 (S-40/41/42 주문 관리)

브랜치: `feature/order-management` / 루트: `C:/projects/northwind/.worktree/feature/order-management`

빌드 검증: `cd frontend; npm run build` → `tsc -b` 타입체크 + `vite build` 모두 통과 (1939 modules transformed, ✓ built). 남은 경고는 기존 chunk size / MUI exceljs eval 경고로 이번 변경과 무관.

## 생성/수정된 파일

### 신규
- `frontend/src/entities/order.ts` — `02_backend_contract.md` 응답 DTO와 1:1 매칭되는 타입 (`OrderStatusIfs`, `OrderDetailStatusIfs`, `CompanyOptionIfs`, `ProductOptionIfs`, `OrderDetailIfs`, `OrderListItemIfs`, `OrderIfs`)
- `frontend/src/pages/orders.tsx` — S-40 주문 목록
- `frontend/src/pages/order-detail.tsx` — S-41 주문 상세 (헤더/항목 상태 변경)
- `frontend/src/pages/order-new.tsx` — S-42 주문 생성

### 수정
- `frontend/src/entities/index.ts` — order 타입 public API export 추가
- `frontend/src/app/provider/redirect-route.tsx` — `OrderRoute` 가드 추가 (`ProductRoute`/`CompanyRoute` 패턴, ADMIN 또는 ORDER)
- `frontend/src/app/router.tsx` — `OrderRoute` 라우트 블록 추가 (`/orders` → `/orders/new` → `/orders/:id` 순서, `/orders/new`를 `:id`보다 먼저 등록)
- `frontend/src/widgets/navbar-left.tsx` — Orders 네비게이션 링크 추가 (ADMIN 또는 ORDER 권한 노출)
- `frontend/CLAUDE.md` — 라우트 보호 표에 `OrderRoute` 행 추가

## 추가된 라우트

| 경로 | 페이지 컴포넌트 | 인증(가드) |
|------|--------------|------|
| `/orders` | `Orders` (orders.tsx) | `OrderRoute` (ADMIN/ORDER) |
| `/orders/new` | `OrderNew` (order-new.tsx) | `OrderRoute` (ADMIN/ORDER) |
| `/orders/:id` | `OrderDetail` (order-detail.tsx) | `OrderRoute` (ADMIN/ORDER) |

## 주요 구현 사항

### S-40 목록 (orders.tsx)
- `companies.tsx` 패턴: 서버 페이지네이션(`paginationMode="server"`), 디바운스(400ms) 고객사명 검색, 상태 탭 필터(`/v1/order-statuses` 동적 로드), 기간 필터(date input 2개 `dateFrom`/`dateTo`).
- 응답 파싱: `ApiIfs<PageIfs<OrderListItemIfs>>` → `body.content` / `body.page.totalElements` (company 목록과 동일한 `PageIfs` 래퍼 구조 사용).
- `totalAmount`는 서버 계산값을 그대로 표시. 컬럼: 주문번호/고객사(클릭 시 상세 이동)/주문일/총액/배송사/상태.
- `[+ New Order]` → `/orders/new`.

### S-41 상세 (order-detail.tsx)
- 기본정보 카드 + 주문 항목 테이블 + 합계.
- **헤더 상태 변경**: 백엔드 전이 규칙(전진만 + CANCELLED, 종료상태 차단)을 클라이언트에서 `allowedNextStatuses()`로 재현해 `<select>` 옵션을 제한 → 불필요한 400 회피. 잘못된 전이는 백엔드가 최종 차단. `PATCH /v1/orders/{id}/status` 후 응답(`OrderRes`)으로 상태 갱신.
- **항목 상태 변경**: 각 행의 `<select>`로 독립 변경, `PATCH /v1/orders/{id}/details/{detailId}/status`.
- 표시: `requiredDate`/`shippedDate`/`paidDate`/`taxStatus`/`paymentType`/`shippingFee`/`notes`. `taxStatus`는 null 허용(`OrderRes.taxStatus`가 nullable).

### S-42 생성 (order-new.tsx)
- lookup: `/v1/orders/company-types`로 타입 로드 후 `isCustomerType`/`isSupplierType` 라벨 매칭(company-detail.tsx 패턴)으로 고객사/배송사 옵션을 각각 `/v1/orders/companies?type=`로 분리 로드. 세금유형 `/v1/orders/tax-statuses`. 상품 `/v1/orders/products?keyword=`(디바운스 400ms).
- 주문 항목: `[+ Add Item]` 행 추가/삭제, 상품 선택 시 단가 자동 입력(읽기전용 표시), 수량/할인율 입력.
- **금액 미리보기**: `roundHalfUp2()`로 소수 2자리 HALF_UP 반올림 → `subtotal = round(unitPrice*qty*(1-discount/100))`, `total = round(sum(subtotal) + shippingFee)`. 백엔드 BigDecimal 계산과 일치.
- 제출: `customerId`/`taxStatusId` 필수 검증, productId 미선택 행은 제외, `shipperId`/`requiredDate`/`paymentType`/`shippingFee`/`notes`는 빈 값 → null. `POST /v1/orders` 성공 시 응답 `OrderRes.id`로 `/orders/:id` 이동.

## 의존 관계 / QA 주의사항

1. **경계면 타입**: `entities/order.ts`의 `~Ifs`는 `02_backend_contract.md`의 응답 DTO 표와 1:1. 특히 `OrderIfs.taxStatus`는 `| null` 허용(계약서 173행). `OrderListItemIfs.status`/`OrderIfs.status`는 `{id, code, name}`, `OrderDetailIfs.status`는 `{id, name}`(code 없음).
2. **필드명 주의**: 생성 요청 키는 `requiredDate`(엔티티 컬럼 `required_date`와 다름). 본문에서 `requiredDate`로 전송 확인 필요.
3. **상태 전이**: 프론트 `<select>`는 백엔드 규칙(rank 전진 + CANCELLED)에 맞춰 옵션 제한. 백엔드 code 값(`PENDING/PAID/SHIPPED/DELIVERED/CANCELLED`)에 의존하므로 시드/코드 불일치 시 옵션이 비게 됨 → 시드 데이터 확인 필요.
4. **금액 일치**: 클라이언트 미리보기와 서버 `totalAmount`가 동일 공식/반올림(HALF_UP, 2자리)을 사용하는지 QA에서 교차 검증 권장.
5. **lookup 권한 경계**: 모든 lookup 호출이 `/v1/orders/*`(ADMIN,ORDER) 경로를 사용 — Company/Product 컨트롤러 직접 호출 없음.
6. **빌드 환경**: worktree에 `node_modules`가 없어 `npm install`(414 packages) 후 `npm run build` 수행함.
