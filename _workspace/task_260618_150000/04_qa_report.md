# QA 리포트

대상: N+1 쿼리 방지 리팩토링 (백엔드 전용, 프론트엔드 변경 없음)

검토 방식: `src/CLAUDE.md` 컨벤션 + `northwind-qa-boundary-check` 체크리스트.
프론트엔드 변경이 없고 API 계약(요청/응답 형식)이 불변이므로 경계면 교차 비교는 N/A.

## Critical 수정 사항

없음. 빌드/런타임 오류 가능성 있는 항목 없음.

## Major 수정 사항

없음. 컨벤션 위반 없음. 구현 검토 결과는 아래와 같다.

- **N+1 쿼리 방지 (핵심 검토 항목)**: 3개 Business 메서드 모두 루프 내 개별 `productService.getProduct()` 호출이 완전히 제거됨.
  - `OrderBusiness.createOrder()` (101-108행): productIds 추출 → `getProducts(productIds)` 1회 배치 → `productMap.get()` 메모리 매핑. 컨벤션 `src/CLAUDE.md` "N+1 쿼리 방지" 패턴과 일치.
  - `PurchaseOrderBusiness.createPurchaseOrder()` (93-100행): 동일 패턴.
  - `StockTakeBusiness.saveStockTakes()` (58-71행): product 조회는 배치화, upsert는 존재 여부 분기 때문에 건별 유지(타당). `saveItem` private 메서드는 인라인되어 루프 내 개별 product 조회가 사라짐.
- **`ProductService.getProducts(List<Long>)` NOT_FOUND 로직 (핵심 검토 항목)**: `findAllById` 1회 조회 후 `Map<Long, ProductEntity>` 변환. `products.size() != ids.stream().distinct().count()` 비교로 누락 ID 감지 → `ApiException(NOT_FOUND)`. distinct 기준 비교라 중복 ID 입력 시 오탐 없음. 기존 `getProduct(Long)`의 NOT_FOUND 동작과 일관됨. 검증 완료(아래 테스트 결과).
- **`@EntityGraph` 미사용 판단 정당함**: 생성/저장 경로 Converter(`OrderDetailConverter.toEntity`, `PurchaseOrderDetailConverter.toEntity`, `StockTakeConverter.toRowResponse`)가 접근하는 ProductEntity 필드는 직접 컬럼(`getId`, `getName`, `getCode`, `getUnitPrice`, `getStandardUnitCost`)뿐이고 LAZY 관계 `productCategory`를 접근하지 않으므로 단순 `findAllById`로 충분. Converter-lazy N+1 위험 없음(교차 확인 완료).
- **레이어 책임**: 배치 조회 메서드가 단일 Repository 접근만 하는 Service에 위치하고, 조합/`@Transactional` 경계는 Business에 유지됨. 혼재 없음.
- **i18n**: NOT_FOUND 메시지는 `ProductErrorConst.NOT_FOUND_ERR_MSG`("error.product.notFound") 상수를 `static import`해 `messageUtil.getMessage()`로 사용. 키 하드코딩 없음. `*ErrorConst` 컨벤션 준수.
- **예외 처리**: `ApiException` + `StatusCode.NOT_FOUND`만 사용.

## Minor 수정 사항

없음. 스타일/가독성 위반 없음.

## 추가 작업 (테스트 보강)

- **`ProductServiceTest` 신규 작성**: 배치 메서드 `getProducts(List<Long>)`에 대한 직접 단위 테스트가 부재했다. 계획서 QA 중점 항목 #1(존재하지 않는 productId → NOT_FOUND)과 프로젝트 컨벤션("새로운 기능에 단위 테스트 포함")을 충족하기 위해 추가했다.
  - `getProducts_batch_returnsMapKeyedById`: 정상 배치 조회 → id 기준 Map 반환.
  - `getProducts_batch_deduplicatesRepeatedIds`: 중복 ID 입력 시 distinct 비교로 NOT_FOUND 오탐이 발생하지 않음(경계 조건).
  - `getProducts_batch_missingId_throwsNotFound`: 누락 ID 포함 시 `ApiException`(NOT_FOUND) 발생.
  - 파일: `src/test/java/com/djccnt15/northwind/domain/product/service/ProductServiceTest.java`

## 테스트 결과

- 백엔드: PASS
  - 실행: `.\gradlew.bat test --tests ProductServiceTest --tests OrderBusinessTest --tests PurchaseOrderBusinessTest --tests StockTakeBusinessTest` → BUILD SUCCESSFUL
  - 신규 `ProductServiceTest` 3건 + 기존 영향 도메인 Business 테스트(배치 create/save 시나리오 포함) 전부 통과.
  - 참고: `@DevTest`(sampledata 시드) 테스트는 실 MySQL(localhost:3306) 연결을 요구하며 현재 환경에 MySQL이 없어 실패하나, 본 리팩토링과 무관(초기화 단계 연결 오류, 코드 로직 실패 아님). 계획서/계약서에 명시된 기지 사항.
- 프론트엔드: N/A (변경 없음 — 백엔드 전용 내부 리팩토링, API 계약 불변)

## 최종 판정

PASS

- 계획서 QA 중점 항목 3건 모두 충족: (1) 배치 조회 NOT_FOUND 예외 발생 — 코드 검토 + 신규 테스트로 검증, (2) 기존 테스트 전부 통과, (3) Business 레이어 루프 내 개별 쿼리 완전 제거.
- 컨벤션 위반 없음. 배치 메서드 테스트 공백만 보강하여 마무리.
