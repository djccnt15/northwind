# QA 리포트 (S-40/41/42 주문 관리)

브랜치: `feature/order-management` / 루트: `C:/projects/northwind/.worktree/feature/order-management`

검토 방식: `02_backend_contract.md` 응답 DTO ↔ `frontend/src/entities/order.ts` 필드 단위 교차 비교 +
`01_plan.md`의 "QA 중점 검토 항목"(권한 경계, 경계면 타입, 상태 전이, 금액 계산, N+1, i18n, 프론트 빌드) 점검.

## Critical 수정 사항

없음. 빌드/런타임 오류를 유발할 결함은 발견되지 않았다.

## Major 수정 사항

없음. 레이어 책임/컨벤션 위반은 발견되지 않았다.

검토 결과 (모두 통과):

- **레이어 책임**: Controller(요청/응답 매핑만) → `OrderBusiness`(`@Business` + `@Transactional`, 복수 Service/Converter 조합) → Service(단일 Repo) → Converter(변환만) 구조가 정확히 지켜짐. cross-domain lookup(`CompanyService`/`CompanyTypeService`/`ProductService`/`TaxStatusService` + 각 Converter)을 `OrderBusiness`에 직접 주입 — `CompanyBusiness` 패턴과 동일.
- **`@Transactional` 위치**: 쓰기 메서드(`createOrder`/`updateOrderStatus`/`updateOrderDetailStatus`)만 Business에 부여. 읽기 메서드는 미부여이나 `findByFilter`(JOIN FETCH)·`findWithDetailById`(@EntityGraph)로 필요한 연관을 모두 페치하므로 트랜잭션 밖 converter 접근에도 LazyInitialization 위험 없음.
- **응답 래퍼**: 모든 엔드포인트가 `ResponseEntity<Api<T>>` 반환(생성은 `Api.CREATED`로 201).
- **예외 처리**: `ApiException` + `StatusCode`만 사용. NOT_FOUND/BAD_REQUEST 분기 정확.
- **권한 경계**: 컨트롤러 클래스 레벨 `@PreAuthorize("hasAnyAuthority('ADMIN', 'ORDER')")` 적용. lookup 엔드포인트(`/orders/companies`, `/orders/products`, `/orders/tax-statuses`, `/orders/company-types`)가 모두 `/api/v1/orders/*` 경로로 `ADMIN,ORDER` 권한 하에 재노출되어 Company/Product 컨트롤러 권한(`ADMIN,COMPANY`/`ADMIN,PRODUCT`)을 침범하지 않음. 프론트 lookup 호출도 모두 `/v1/orders/*` 경유.
- **경계면 타입 1:1 일치** (필드명·타입·nullable 모두 대조):
  - `OrderRes` ↔ `OrderIfs`: 18개 필드 일치. `taxStatus`/`shipper`/`requiredDate`/`shippedDate`/`paidDate`/`shippingFee`/`taxRate`/`paymentType`/`notes` nullable 일치. `requiredDate`(JSON 키) = 엔티티 `invoiceDate` 필드(@Column `required_date`)로 정확히 매핑됨.
  - `OrderListRes` ↔ `OrderListItemIfs`: `status` = `{id,code,name}`(`OrderStatusRef`), `shipperName` nullable 일치.
  - `OrderDetailRes` ↔ `OrderDetailIfs`: `status` = `{id,name}`(`OrderDetailStatusRef`, code 없음) 일치, `subtotal`/`unitPrice`(BigDecimal→number) 일치.
  - `*OptionRes`/`OrderStatusRes`/`OrderDetailStatusRes` ↔ 대응 Ifs 일치. `CompanyTypeRes{id,companyType}`/`TaxStatusRes{id,status}` 재사용 타입도 일치.
- **상태 전이 검증**: 백엔드 `OrderService.validateStatusTransition`(동일 상태 차단 / 종료상태(DELIVERED·CANCELLED) 전이 차단 / CANCELLED 예외 허용 / rank 전진만)과 프론트 `order-detail.tsx`의 `allowedNextStatuses()` 규칙이 동일. 프론트는 `<select>` 옵션 제한으로 비정상 전이를 사전 차단하고, 백엔드가 400(`error.order.invalidStatusTransition`)으로 최종 차단.
- **금액 계산 일치**: 백엔드 `subtotal = unitPrice*qty*(1-discount/100)`, 소수 2자리 HALF_UP(`OrderDetailConverter.calculateSubtotal`), `totalAmount = sum(subtotal)+shippingFee`. 목록은 N+1 회피를 위해 `findTotalAmountByIdIn` 집계 쿼리로 일괄 계산. 프론트 `order-new.tsx`의 `roundHalfUp2`/`lineSubtotal`이 동일 공식·반올림 사용.
- **N+1**: 목록(`findByFilter`)은 `JOIN FETCH customer/orderStatus` + `LEFT JOIN FETCH shipper` + 별도 배치 집계로 처리. 상세(`findWithDetailById`)는 `@EntityGraph`로 전 연관 페치. 컬렉션 페치 + 페이지네이션 동시 사용 회피.
- **i18n**: `OrderModelConst`/`OrderDetailModelConst`(`{validation.*}` 보간형) ↔ `OrderErrorConst`/`OrderDetailErrorConst`(평문 키) 분리. `messages*.properties`/`errors*.properties` 기본·`_ko` 번들 모두 키 존재. 키 하드코딩 없이 `static import` 상수 사용. `@WebMvcTest`에 `@MockitoBean MessageUtil` 주입됨.
- **FSD/프론트 컨벤션**: 역방향 참조 없음(`pages→entities/shared`). `~Ifs`는 `entities/`에만 정의(페이지 인라인 정의 없음, `order-new.tsx`의 `OrderItemRow`는 폼 로컬 상태 타입으로 API 경계와 무관). `import type` 사용, 강제 `as` 캐스팅 없음, API 호출 `.then().catch().finally()` 체이닝, `privateApi` 일관 사용. 라우터 순서(`/orders/new` < `/orders/:id`) 및 `OrderRoute` 가드(ADMIN/ORDER) 정확.

## Minor 수정 사항

없음(수정 적용). 검토 중 확인한 사항:

- 권한 경계 음성 케이스(`ORDER`/`ADMIN` 외 권한 → 403) 회귀 테스트를 추가 시도했으나, `@WebMvcTest` 슬라이스는 애플리케이션 `AuthConfig`의 `@EnableMethodSecurity`를 로드하지 않아 메서드 레벨 `@PreAuthorize`가 강제되지 않는다(MANAGER 권한이 핸들러에 도달해 200 반환). 슬라이스 설계상 의도된 동작이므로 추가 테스트는 되돌렸고, 컨트롤러 클래스의 `@PreAuthorize("hasAnyAuthority('ADMIN','ORDER')")` + 앱 전역 `@EnableMethodSecurity`로 런타임 권한 경계가 보장됨을 코드 검증으로 확인. 기존 테스트는 401(미인증)·정상 권한 200/201·검증 1400을 커버.

## 테스트 결과

- **백엔드**: PASS — `.\gradlew.bat test` BUILD SUCCESSFUL(exit 0). order 도메인 Service/Business/Controller 테스트 포함 전체 통과(기존 컨텍스트 캐시 공유로 H2 중복 시드 충돌 없음).
- **프론트엔드**: PASS — `cd frontend; npm run build`(`tsc -b` 타입체크 + `vite build`) 성공, 1939 modules transformed, ✓ built. 남은 경고(chunk size 500kB 초과, MUI exceljs `eval`)는 기존 의존성 경고로 이번 변경과 무관.

## 최종 판정

**PASS**

경계면 타입(필드명/타입/nullable), 권한 경계, 상태 전이, 금액 계산/반올림, N+1 방지, i18n 분리, 레이어 책임, FSD 컨벤션이 모두 계약·컨벤션과 일치하며 백엔드 테스트·프론트 빌드 모두 통과한다. 수정이 필요한 위반 사항은 발견되지 않았다.
