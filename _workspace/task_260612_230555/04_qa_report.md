# QA 리포트 — 발주 관리 (Purchase Order, S-50/51/52)

검증 방식: `02_backend_contract.md` 응답 필드 ↔ `frontend/src/entities/purchase-order.ts` `~Ifs` 필드 단위 교차 비교 + 백엔드/프론트엔드 컨벤션 체크리스트 + 01_plan.md "QA 중점 검토 항목" 7개.

## Critical 수정 사항

없음. 빌드/런타임 오류를 유발하는 위반은 발견되지 않았다.

## Major 수정 사항

없음. 컨벤션 위반(레이어 책임 혼재, i18n 분리 누락, 권한 불일치, 경계면 필드 불일치 등)은 발견되지 않았다.

## Minor 수정 사항

1. **(수정 안 함 — 기존 패턴 일관성)** `frontend/src/pages/purchase-order-new.tsx`(L25-30)에 폼 로컬 상태용 인터페이스 `PurchaseItemRow`를 컴포넌트 파일 내부에 인라인 정의함. `frontend/CLAUDE.md`의 "인터페이스는 `entities/`에만 정의" 규칙과 형식상 어긋난다.
   - **판정**: 미러링 원본인 `frontend/src/pages/order-new.tsx`도 동일하게 `OrderItemRow`를 인라인 정의(L26)하고 있어, 이는 기존 코드베이스에 이미 확립된 "폼 입력 행(모든 필드 string)은 API 엔티티(`~Ifs`)가 아닌 로컬 폼 상태 셰이프"로 취급하는 패턴이다. 발주 작업에서 새로 도입한 위반이 아니며, `entities/`로 이동시키면 오히려 order 페이지와 패턴이 갈라진다. 일관성 유지를 위해 그대로 둔다. (order 도메인 전반의 후속 정리 대상으로 기록)

## 경계면 교차 비교 결과 (필드 단위)

`02_backend_contract.md` 응답 DTO ↔ `purchase-order.ts` 전 필드 1:1 일치 확인:

| 백엔드 응답 DTO | 프론트 인터페이스 | 결과 |
|----------------|------------------|------|
| `PurchaseOrderStatusRes`(id/code/name/sortOrder) | `PurchaseOrderStatusIfs` | 일치 (sortOrder `string\|null`) |
| `PurchaseOrderStatusRef`(id/code/name) | `PurchaseOrderStatusRefIfs` | 일치 |
| `CompanyOptionRes`(id/name) | `VendorOptionIfs` | 일치 |
| `ProductCostOptionRes`(id/name/standardUnitCost) | `ProductCostOptionIfs` | 일치 |
| `PurchaseOrderListRes`(id/submittedDate/vendorName/status/totalAmount) | `PurchaseOrderListItemIfs` | 일치 (vendorName `string\|null`) |
| `PurchaseOrderDetailRes`(id/product/unitPrice/quantity/subtotal) | `PurchaseOrderDetailIfs` | 일치 (discount/품목status 없음) |
| `PurchaseOrderRes`(전 16필드) | `PurchaseOrderIfs` | 일치 (nullable 필드 모두 `\| null`) |

- 인증 엔드포인트(`/api/v1/*`) 전부 `privateApi`로 호출됨 — 확인.
- 응답 래퍼 `ApiIfs<T>`/`PageIfs<T>` 사용 일관 — 목록은 `ApiIfs<PageIfs<...>>`, 단건은 `ApiIfs<...>`로 정확히 구분.
- 요청 바디(`PurchaseOrderCreateReq`/`PurchaseOrderStatusUpdateReq`)도 계약과 일치 (vendorId/purchaseOrderDetails[{productId,quantity,unitPrice}], statusId/payment*).

## 01_plan.md QA 중점 항목 점검

1. **경계면 필드 일치**: 위 표 참조 — 전부 일치. PASS.
2. **상태 전이 rank-map / REJECTED**: `PurchaseOrderService.validateStatusTransition`에서 (a) 동일 상태 거부, (b) 종료 상태(PAID/REJECTED) 추가 전이 차단, (c) REJECTED는 `REJECTABLE_FROM=(DRAFT,PENDING_APPROVAL)`에서만 도달, (d) 그 외 `STATUS_RANK` 순방향(`nextRank > currentRank`)만 허용 — 계약/order 패턴과 일관. 프론트 `purchase-order-detail.tsx`의 `STATUS_RANK`/`TERMINAL_CODES`/`REJECTABLE_CODES`도 동일 규칙 미러링(노출용)이며 최종 검증은 서버. PASS.
3. **i18n `*ModelConst`/`*ErrorConst` 분리**: `PurchaseOrderModelConst`/`PurchaseOrderDetailModelConst`는 `"{validation.*}"` 키(Bean Validation message), `PurchaseOrderErrorConst`/`PurchaseOrderDetailErrorConst`는 평문 키(`error.purchaseOrder.*`, messageUtil용)로 정확히 분리. 메시지 키 하드코딩 없음(모두 static import). `messages(_ko).properties`·`errors(_ko).properties` 4개 파일 모두 키 등록 확인. PASS.
4. **권한 일치**: 백엔드 컨트롤러 `@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")`, 프론트 `PurchaseRoute`(ADMIN 또는 PURCHASE), navbar 메뉴 노출 조건(ADMIN 또는 PURCHASE) 3자 일치. ADMIN 누락(S-60/61/62 재발 패턴) 없음. PASS.
5. **발주 단가 = standardUnitCost(원가)**: `ProductCostOptionConverter`는 `standardUnitCost`만 매핑(판매가 unitPrice 미사용), `PurchaseOrderDetailConverter.toEntity`의 unitPrice 기본값 = `product.getStandardUnitCost()`, 프론트 `onSelectProduct`도 `standardUnitCost`를 단가 자동입력. PASS.
6. **N+1 방지**: `PurchaseOrderRepo.findWithDetailById`가 `@EntityGraph`로 vendor/status/submittedBy/approvedBy/purchaseOrderDetails(+product) 일괄 로드. 목록은 `findByFilter`(JOIN FETCH vendor/status/submittedBy) + `findTotalAmountByIdIn`(id 배치 집계 쿼리)로 컬렉션 페치 없이 totalAmount 계산 → 페이지네이션 N+1 회피. PASS.
7. **테스트**: 서비스/비즈니스/컨트롤러 3계층 테스트 존재 + 전체 통과. PASS.

## 테스트 결과

- 백엔드: **PASS** — `.\gradlew.bat test -x buildFrontend` BUILD SUCCESSFUL (전체 통과).
- 프론트엔드: **PASS** — `cd frontend; npm run build` (`tsc -b && vite build`) 성공. 타입 오류 0. 출력 경고는 모두 pre-existing 라이브러리/번들 크기(@mui exceljs eval, 500kB chunk)로 구현 코드와 무관.

## 최종 판정

**PASS**

- 경계면(필드명/타입/nullable) 전부 일치, 백엔드 레이어 책임·i18n 분리·권한·N+1·단가 기준·상태 전이 규칙 모두 컨벤션 및 계약 준수.
- 발견된 유일한 형식적 deviation(`PurchaseItemRow` 인라인 인터페이스)은 미러링 원본(order 도메인)과 동일한 기존 확립 패턴으로, 발주 작업이 새로 도입한 위반이 아니므로 일관성 유지를 위해 보존. Critical/Major 없음.
