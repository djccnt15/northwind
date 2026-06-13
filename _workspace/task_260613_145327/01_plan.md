# 구현 계획

## 핵심 설계 결정

- **"전산 재고(예상)" = 해당 상품의 가장 최근 `stock_take.quantity_on_hand`** (없으면 0). `product` 테이블에 현재 재고 컬럼이 없으므로 새 컬럼/ERD 변경 없이 `stock_take` 히스토리만으로 처리한다.
- **Upsert**: `(product, stockTakeDate)` 조합이 이미 존재하면 `quantityOnHand`만 갱신, 없으면 신규 생성하면서 `expectedQuantity`를 "그 시점까지의 최신 quantityOnHand"로 고정 기록.
- **낙관적 락**: `StockTakeEntity`에 `@Version private Long version;` 추가. 갱신 시 버전 충돌이면 `ApiException(BAD_REQUEST, ...)`로 변환.

## 백엔드 작업 항목

1. **Entity 수정** — `db/entity/StockTakeEntity.java`
   - `@Version private Long version;` 필드 추가 (낙관적 락)

2. **Projection** — `db/projection/StockTakeLatestProjection.java` (신규)
   - `Long getProductId();`, `Long getQuantityOnHand();`

3. **Repository** — `db/repository/StockTakeRepo.java` 보강
   - `findLatestByProductIds(List<Long> productIds)`: 상품별 최신(`MAX(id)`) `stock_take` 레코드의 `quantityOnHand`을 `StockTakeLatestProjection` 리스트로 반환하는 JPQL (서브쿼리로 상품별 최신 id 그룹화 — N+1 방지)
   - `findByProductIdInAndStockTakeDate(List<Long> productIds, LocalDate date)`: 오늘 날짜의 기존 실사 초안(draft) 레코드 목록 조회
   - `findByProductAndStockTakeDate(ProductEntity product, LocalDate date)`: 저장(upsert) 시 기존 레코드 존재 여부 확인용

4. **신규 도메인 패키지** `domain/stocktake/`
   - `model/StockTakeRowRes.java` (`@Data @Builder`): `productId`, `productCode`, `productName`, `expectedQuantity`(Long), `quantityOnHand`(Long, nullable — 오늘 작성한 초안이 없으면 null)
   - `model/StockTakeItemReq.java` (`@Data @AllArgsConstructor`): `productId`(`@NotNull`), `quantityOnHand`(`@NotNull @PositiveOrZero`)
   - `model/StockTakeSaveReq.java` (`@Data @AllArgsConstructor`): `stockTakeDate`(`LocalDate`, `@NotNull`), `items`(`List<StockTakeItemReq>`, `@NotEmpty @Valid`)
   - `converter/StockTakeConverter.java` (`@Converter`): `ProductEntity` + `expectedQuantity` + `draftQuantityOnHand(nullable)` → `StockTakeRowRes`
   - `validation/StockTakeModelConst.java`, `validation/StockTakeErrorConst.java`: 검증 상수 + 에러 메시지 키(`QUANTITY_NOT_NULL_MSG`, `QUANTITY_MIN_MSG`, `CONFLICT_ERR_MSG` 등). `messages.properties`/`messages_ko.properties`에 대응 키 추가
   - `service/StockTakeService.java` (`@Service`, 단일 리포지토리 `StockTakeRepo` 접근): 최신 수량 조회, draft 조회, upsert 단건 저장(+낙관적 락 충돌 시 예외 변환)
   - `business/StockTakeBusiness.java` (`@Business`, `@Transactional`): `ProductService`(또는 `ProductRepo` 페이지네이션) + `StockTakeService` 조합
     - `getStockTakeRows(keyword, pageable)`: 상품 페이지 조회 → 해당 페이지 상품ID들로 최신 수량/오늘 draft 조회 → `StockTakeRowRes` 페이지 구성
     - `saveStockTakes(StockTakeSaveReq req)`: 각 item에 대해 upsert 수행 후 갱신된 `StockTakeRowRes` 목록 반환
   - `controller/StockTakeApiController.java` (`@RestController`, `@PreAuthorize("hasAnyAuthority('ADMIN','STOCK')")`)
     - `GET {API_V1}/stock-takes?keyword=&page=&size=` → `Page<StockTakeRowRes>`
     - `POST {API_V1}/stock-takes` (body: `StockTakeSaveReq`, `@Validated`) → `List<StockTakeRowRes>`

5. **테스트** — `StockTakeBusinessTest` (또는 Service 통합 테스트, `@SpringBootTest` + `@Transactional`)
   - 최초 조회 시 `expectedQuantity = 0` (이전 실사 없음)
   - 저장 후 동일 상품 재조회 시 `expectedQuantity`가 직전 `quantityOnHand`로 갱신됨
   - 같은 날 재저장 시 upsert(레코드 1건 유지, `quantityOnHand` 갱신)
   - 낙관적 락 충돌(버전 불일치) 시 `BAD_REQUEST` 응답
   - 컨트롤러 권한 테스트: `ADMIN`/`STOCK` 외 권한은 403

## 프론트엔드 작업 항목

1. **타입 정의** — `entities/` (기존 product 관련 타입 파일에 추가 또는 신규 `entities/stock-take.ts` + `entities/index.ts`에서 export)
   - `StockTakeRowIfs`: `{ productId: number; productCode: string; productName: string; expectedQuantity: number; quantityOnHand: number | null }`

2. **라우트 가드** — `app/provider/redirect-route.tsx`
   - `StockRoute` 신규 추가: `ADMIN` 또는 `STOCK` 권한 없으면 `/home`으로 리다이렉트 (`ProductRoute`/`OrderRoute` 패턴 그대로 따름)

3. **페이지** — `pages/stock-take.tsx` (신규)
   - `products.tsx`의 필터바 + 서버 페이지네이션 DataGrid 패턴 참고
   - 컬럼: 상품코드, 상품명, 전산 재고(예상, 읽기전용), 실사 재고(실제, 편집 가능 number 입력), 차이(= 실사재고 - 전산재고, 계산값, 0이 아니면 강조 표시)
   - `quantityOnHand`이 null인 행은 기본값으로 `expectedQuantity`를 입력 칸에 표시(편집 시작점)
   - 행별 편집값을 로컬 state(Map<productId, number>)로 관리, "실사 결과 저장 및 재고 조정" 버튼 클릭 시 변경된 행만 모아 `POST /v1/stock-takes`로 일괄 전송 (`{ stockTakeDate: 오늘(YYYY-MM-DD), items: [...] }`)
   - 저장 성공 시 응답으로 받은 `StockTakeRowRes` 목록으로 해당 행들 갱신, 실패(1400 검증 오류 / 400 낙관적 락 충돌) 시 에러 메시지 표시

4. **라우터** — `app/router.tsx`
   - `StockRoute`로 감싼 라우트 그룹 추가: `{ path: "/stock-take", element: <StockTake /> }`

5. **네비게이션** — `widgets/navbar-left.tsx`
   - "상품/재고" 메뉴 그룹에 "재고 실사" 링크 추가, `ADMIN` 또는 `STOCK` 권한 노출 (Products 링크의 `ProductRoute` 권한 체크 패턴 참고)

6. **문서** — `frontend/CLAUDE.md` 라우트 가드 표에 `StockRoute` 행 추가 (`OrderRoute`/`PurchaseRoute`/`CompanyRoute` 행과 동일 형식)

## 참고 패턴

- 백엔드 신규 도메인 스캐폴딩 전체 흐름: `domain/purchase/*` (가장 최근 추가된 복잡 도메인 — Business 레이어 + 여러 모델/컨버터 구조)
- 프론트 DataGrid + 필터 + 서버 페이지네이션: `pages/products.tsx`
- 라우트 가드 추가: `app/provider/redirect-route.tsx`의 `OrderRoute`/`PurchaseRoute` (가장 최근 추가됨, ADMIN-or-X 패턴)

## doc/ 영향 범위

- `doc/StoryBoard.md`: S-63 "재고 실사" 상태 `🔲` → `✅` 갱신. 섹션 6(미구현 화면 개발 우선순위) 표에서 S-63 행을 "구현 완료되어 제외" 처리 (S-30/31/32 사례와 동일한 패턴)
- `doc/PRD.md` / `doc/EDR.md`: 변경 없음 (기존 `stock_take` 테이블 스키마 그대로 사용, `@Version` 컬럼은 ERD에 새 컬럼이지만 감사 컬럼처럼 다이어그램에 명시되지 않는 기존 관례를 따름 — `created_by`/`updated_at` 등도 ERD에는 있으나 BaseEntity 공통 필드로 처리됨. `version` 컬럼 추가가 ERD와 크게 어긋난다고 판단되면 `EDR.md`의 `stock_take` 블록에 `bigint version` 한 줄 추가)

## QA 중점 검토 항목

- `StockTakeRowRes` 필드 ↔ 프론트 `StockTakeRowIfs` 필드 1:1 일치 (특히 `quantityOnHand`의 nullable 여부)
- "전산 재고(예상)" 계산 로직(최신 `stock_take.quantity_on_hand`, 없으면 0)이 백엔드 테스트로 검증되는지
- 낙관적 락 충돌 처리(`ObjectOptimisticLockingFailureException` → `ApiException(BAD_REQUEST)`)와 프론트 에러 표시
- `findLatestByProductIds` 쿼리의 N+1 여부 (페이지당 1회 쿼리인지)
- `@PreAuthorize("hasAnyAuthority('ADMIN','STOCK')")` 적용 및 `StockRoute` 가드 일치
- *ModelConst/*ErrorConst 분리, `messages.properties`/`messages_ko.properties` 키 추가 여부
- Business(`@Transactional`) ↔ Service(단일 리포지토리) 책임 분리 준수
