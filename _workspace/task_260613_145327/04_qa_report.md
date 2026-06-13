# QA 리포트 — 재고 실사 (S-63)

검토 대상: `02_backend_contract.md`, `03_frontend_summary.md`, `01_plan.md`의 "QA 중점 검토 항목"
검토 방식: 경계면 교차 비교(`StockTakeRowRes` ↔ `StockTakeRowIfs`) + 백엔드/프론트엔드 컨벤션 체크리스트 + 테스트/빌드 실행

## Critical 수정 사항

없음. (빌드/런타임 오류 가능 항목 없음)

## Major 수정 사항

없음. 컨벤션 위반 없음. 주요 확인 결과는 아래와 같다.

- **경계면 1:1 일치**: `StockTakeRowRes`(productId:Long, productCode:String, productName:String, expectedQuantity:Long non-null, quantityOnHand:Long nullable) ↔ `StockTakeRowIfs`(productId:number, productCode:string, productName:string, expectedQuantity:number, quantityOnHand:number|null) 필드명·타입·nullable 모두 일치. `quantityOnHand`의 nullable 처리가 양쪽 모두 정확.
- **응답 래퍼 일관성**: GET은 `Api<Page<StockTakeRowRes>>` ↔ 프론트 `ApiIfs<PageIfs<StockTakeRowIfs>>`, POST는 `Api<List<StockTakeRowRes>>` ↔ `ApiIfs<StockTakeRowIfs[]>`로 일관.
- **인증 경계**: `/api/v1/stock-takes`(인증 필요)를 프론트에서 `privateApi`로 호출. 컨트롤러 `@PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK')")` ↔ 프론트 `StockRoute`(ADMIN 또는 STOCK) ↔ navbar 노출 조건 ↔ `frontend/CLAUDE.md` 라우트 가드 표 모두 일치.
- **레이어 책임 분리**: Controller(요청/응답·`@PreAuthorize`·`@Validated`만) / Business(`@Business` + `@Transactional`, ProductService+StockTakeService+Converter 조합) / Service(단일 `StockTakeRepo` 접근 + 낙관적 락 예외 변환) / Converter(`@Converter`, 변환만). 혼재 없음. 복잡 도메인에 Business 존재 — 적절.
- **모든 엔드포인트가 `ResponseEntity<Api<T>>` 반환**, 예외는 `ApiException` + `StatusCode`만 사용.
- **N+1 방지**: `getStockTakeRows`는 상품 페이지 1쿼리 + `findLatestByProductIds`(서브쿼리 `MAX(id)` 그룹화) 1쿼리 + `findDraftByProductIdsAndStockTakeDate` 1쿼리로 페이지당 고정 쿼리 수. 빈 리스트 가드(`productIds.isEmpty()`)도 존재.
- **낙관적 락**: `StockTakeEntity.@Version`, Service `upsert`에서 `OptimisticLockingFailureException` → `ApiException(BAD_REQUEST, CONFLICT_ERR_MSG)` 변환. 프론트는 1400(검증)과 그 외(400 등 `result.description`) 분기 처리.
- **i18n 분리**: `StockTakeModelConst`(`{validation.stockTake.*}` 보간 키) / `StockTakeErrorConst`(`error.stockTake.*` 평문 키) 분리. `messages(.ko)`·`errors(.ko)` 키 모두 존재. 테스트 프로파일 `application.yaml`에 `basename: messages,errors` 설정 확인. `@WebMvcTest`에 `@MockitoBean MessageUtil` 존재.
- **"전산 재고(예상)" 로직 검증**: `StockTakeBusinessTest`에 (1) 최초 expected=0/draft=null, (2) 저장 후 draft 반영, (3) 같은 날 upsert(단일 레코드, expected 불변), (4) 어제→오늘 시 expected가 직전 quantityOnHand로 설정됨까지 모두 커버. `StockTakeServiceTest`에 `@Version` 낙관적 락 충돌 케이스 존재.
- **FSD 준수**: 타입은 `entities/stock-take.ts`에만 정의(`~Ifs` 접미사) + `entities/index.ts` re-export, 인라인 인터페이스 없음, `import type` 사용, API 호출 `.then().catch().finally()` 체이닝, 레이어 역방향 참조 없음. `as` 강제 캐스팅은 `edits.get(...) as number` 1곳뿐인데 직전 `edited === undefined` 필터로 존재가 보장된 값이라 안전(미사용/위험 캐스팅 아님).

## Minor 수정 사항

- **[수정함] 미사용 i18n 상수/키 제거**: `StockTakeErrorConst.PRODUCT_NOT_FOUND_ERR_MSG`(`error.stockTake.productNotFound`)가 어디에서도 참조되지 않았다. 존재하지 않는 productId 저장 시 404는 `ProductService.getProduct()`가 `error.product.notFound`로 처리하며(계약 48번 줄에 명시), `error.stockTake.productNotFound`는 dead code였다.
  - 이유: 컨벤션상 사용되지 않는 상수/메시지 키는 혼란을 유발하므로 제거. 실제 동작(404 처리)은 `ProductService` 경로로 변함없이 동작.
  - 수정 파일: `StockTakeErrorConst.java`(상수 1개 삭제), `errors.properties`/`errors_ko.properties`(`error.stockTake.productNotFound` 키 각 1줄 삭제). `CONFLICT_ERR_MSG`/`error.stockTake.conflict`는 실사용되므로 유지.
  - 검증: 제거 후 백엔드 테스트 재실행 `BUILD SUCCESSFUL`.

> 참고(수정 아님): 백엔드 `getStockTakeRows`는 keyword가 비어도 `"%%"` 패턴으로 전체 매칭, 프론트는 빈 keyword를 아예 전송하지 않음(기본값 `""`) — 양쪽 모두 "전체 조회"로 귀결되어 동작상 일치. 기존 `ProductBusiness`와 동일한 LIKE 패턴 관례를 따름.

## 테스트 결과

- **백엔드**: PASS — `gradlew -p <worktree> test -x buildFrontend` → `BUILD SUCCESSFUL` (Minor 수정 전·후 2회 모두 통과). 로그의 "OpenJDK ... Sharing" 라인은 JVM 경고일 뿐 실패 아님.
- **프론트엔드**: PASS — `cd frontend; npm run build`(`tsc -b && vite build`) → `✓ built`. 타입 체크 통과 + 번들 생성. chunk-size/eval(MUI exceljs)/babel-timing 경고는 기존부터 존재하며 본 변경과 무관. (PowerShell `NativeCommandError`는 네이티브 stderr 래핑 아티팩트로 빌드 실패 아님.)

## 최종 판정

**PASS**

경계면 타입 일치, 레이어 책임 분리, i18n 분리, 인증 가드 일관성, N+1 방지, 낙관적 락 처리, 테스트 커버리지 모두 컨벤션을 충족한다. 발견된 유일한 Minor(미사용 i18n 상수/키)는 직접 제거 후 재검증 완료. 백엔드 테스트·프론트엔드 빌드 모두 통과.
