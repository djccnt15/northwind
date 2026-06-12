# 요구사항

## 기능 설명

StoryBoard S-40/41/42 "주문 관리(Order-to-Cash)" 화면을 풀스택으로 구현한다.

- **S-40 주문 목록** (`/orders`, ADMIN/ORDER): 주문 목록 조회, 상태별 필터, 기간 필터, 검색(고객사명), 서버 사이드 페이지네이션
- **S-41 주문 상세** (`/orders/:id`, ADMIN/ORDER): 주문 헤더 정보 표시, 주문 항목(order_detail) 목록, 헤더 상태 변경(접수→결제완료→출고→배송완료, 또는 →취소), 항목별 독립 상태 변경(부분 취소/반품)
- **S-42 주문 생성** (`/orders/new`, ADMIN/ORDER): 고객사/배송사 선택, 주문 항목 추가(상품 선택 시 단가 자동입력, 수량, 할인율), 총액 자동계산, 주문 등록

## 범위: 풀스택

## 참고 도메인 (유사한 기존 구현)

- **백엔드**: `domain/company/*` (S-30/31/32) — 페이지네이션 목록(`findByFilter`), 상세 조회(`findWithRelationById` + `@EntityGraph`), CRUD 패턴, 도메인 간 서비스 재사용 사례(`CompanyBusiness`가 `domain/tax`의 `TaxStatusService`/`TaxStatusConverter`를 직접 주입해서 사용)
- **백엔드**: `domain/product/*` (S-60/61/62) — 단일 리소스 상세+수정 패턴(`ProductApiController`), discontinue 같은 상태 전환 패턴
- **프론트엔드**: `pages/companies.tsx` + `pages/company-detail.tsx` — DataGrid 목록(서버 페이지네이션, 디바운스 검색, 탭 필터) + 상세 페이지(읽기/수정 토글, 연관 데이터 섹션 fetch)
- **프론트엔드**: `app/provider/redirect-route.tsx`의 `ProductRoute`/`ManagerRoute` — 권한 가드 추가 패턴

## DB 엔티티/레포지토리 현황 (이미 존재함 — 신규 생성 불필요)

- Entity: `OrdersEntity`, `OrderDetailEntity`, `OrderStatusEntity`, `OrderDetailStatusEntity`, `TaxStatusEntity`(`domain/tax` 소속)
- Repository: `OrdersRepo`(`findByCustomerIdOrderByOrderDateDesc` 존재), `OrderDetailRepo`, `OrderStatusRepo`, `OrderDetailStatusRepo`, `TaxStatusRepo` — 모두 `JpaRepository`만 상속한 빈 스텁이라 목록/검색용 쿼리 메서드 추가 필요
- Validation 스텁: `domain/order/validation/OrderStatusModelConst`(CODE_MAX_LENGTH=20, NAME_MAX_LENGTH=50), `OrderDetailStatusModelConst`(NAME_MAX_LENGTH=50) — 이미 엔티티에서 참조 중

## 권한 / 라우팅

- StoryBoard 기준 접근 권한: `ADMIN, ORDER`
- `global/constants/RoleConst.java`에 `ORDER` 상수 이미 존재 (최근 권한 체계 정리 시 추가됨)
- 백엔드: `OrderApiController`에 `@PreAuthorize("hasAnyAuthority('ADMIN', 'ORDER')")` 적용 (`CompanyApiController`/`ProductApiController` 패턴 동일)
- 프론트엔드: `redirect-route.tsx`에 `OrderRoute` 가드 신설 (`ProductRoute` 패턴 동일), `router.tsx`/`navbar-left.tsx`/`frontend/CLAUDE.md`에 반영

## 중요 설계 결정 — 권한 경계를 넘는 조회(lookup) 데이터

S-42(주문 생성) 폼은 거래처(고객사/배송사)·상품·세금유형·주문상태를 선택해야 하지만, 이 데이터들은 각각 `CompanyApiController`(`ADMIN,COMPANY`), `ProductApiController`(`ADMIN,PRODUCT`)에 속해 있어 `ADMIN,ORDER`만 가진 사용자는 호출할 수 없다.

→ **해결 방향**: `CompanyBusiness`가 `domain/tax`의 `TaxStatusService`/`TaxStatusConverter`를 직접 주입해 재사용하는 기존 패턴과 동일하게, `OrderBusiness`가 `domain/company`의 `CompanyService`/`CompanyTypeService`/`CompanyConverter`/`CompanyTypeConverter`와 `domain/product`의 `ProductService`/`ProductConverter`, `domain/tax`의 `TaxStatusService`/`TaxStatusConverter`를 직접 주입하여 **`OrderApiController` 자신의 권한(`ADMIN,ORDER`) 하에 경량 lookup 엔드포인트를 노출**한다.

- `GET /orders/company-types` → `List<CompanyTypeRes>` (재사용)
- `GET /orders/companies?type={typeId}&keyword=` → `List<CompanyOptionRes>` (id, name) — 신규 경량 DTO, `companyService.getCompanies()` 재사용 후 변환
- `GET /orders/products?keyword=` → `List<ProductOptionRes>` (id, name, unitPrice) — 신규 경량 DTO, `productService.getProducts()` 재사용 후 변환 (discontinued=false만)
- `GET /orders/tax-statuses` → `List<TaxStatusRes>` (재사용)
- `GET /order-statuses` → `List<OrderStatusRes>` (id, code, name, sortOrder)
- `GET /order-detail-statuses` → `List<OrderDetailStatusRes>` (id, name, sortOrder)

이 설계는 QA 단계에서 "도메인 책임 경계" 관점으로 한 번 더 검토 필요 — `01_plan.md`의 QA 중점 검토 항목에 기재.

## 마스터 데이터(상태값) 시드

`order_status`/`order_detail_status` 테이블은 현재 시드 데이터가 없다 (`src/test/resources/data-h2.sql`에 없음, Flyway/Liquibase 미사용).

- **order_status** (code/name/sort_order): `PENDING`/접수(1), `PAID`/결제완료(2), `SHIPPED`/출고(3), `DELIVERED`/배송완료(4), `CANCELLED`/취소(5)
- **order_detail_status** (name/sort_order): 대기(1), 출고(2), 취소(3)
- 테스트(H2)에는 `data-h2.sql`에 INSERT 추가
- 개발 MySQL DB 시드 방법은 백엔드 에이전트가 기존 패턴(`DataLoader`의 `@EventListener(ApplicationReadyEvent.class)`) 활용 여부를 판단하여 결정

## 비즈니스 규칙 (StoryBoard 기반)

- 주문 생성 시: `orderDate`=오늘, `appUser`=현재 로그인 사용자, `orderStatus`=PENDING(접수), 각 `orderDetail.status`=대기, `orderDetail.unitPrice`/`standardUnitCost`는 생성 시점 상품 가격을 스냅샷으로 저장
- 항목별 소계(subtotal) = `unitPrice * quantity * (1 - discount/100)`
- 주문 총액(totalAmount) = `sum(subtotal) + shippingFee` (taxRate 적용 여부는 `taxStatus` 기반 — 면세/과세 텍스트는 `tax_status.status` 값으로 판별, `company-detail.tsx`의 `isCustomerType`/`isSupplierType` 라벨 매칭 패턴 참고)
- 헤더 상태 전이: 접수→결제완료→출고→배송완료, 또는 (대부분 단계에서) →취소. 상태 변경 시 연관 날짜 필드 자동 기록: PAID→`paidDate`=오늘, SHIPPED→`shippedDate`=오늘 (정확한 매핑은 백엔드 에이전트가 `order_status.code` 기준으로 구현)
- 항목별 상태 변경(부분 취소/반품): `order_detail_status` 독립 변경, 헤더 상태와 무관

## 특이사항

- `OrdersRepo.findByCustomerIdOrderByOrderDateDesc`(S-31 거래처 상세의 주문 이력에서 사용 중)는 그대로 유지 — 변경/삭제 금지
- `domain/company/converter/OrderSummaryConverter`, `OrderSummaryRes`는 S-31 전용이며 이번 작업과 별개 — 재사용하지 않고 새 `OrderConverter`/`OrderRes` 등을 만든다
- 주문일/배송일 등은 `date`(LocalDate) — 별도 타임존 변환 불필요(기존 패턴과 동일)

## Worktree
- 브랜치: feature/order-management
- 경로: C:/projects/northwind/.worktree/feature/order-management
