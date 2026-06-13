# 백엔드 API 계약 — 재고 실사 (S-63)

## 개요

전산 재고(이전 실사 결과)와 실제 창고 재고(이번 실사 입력값)를 비교·조정하는 재고 실사 기능. `domain/stocktake/*` 도메인 신설 (Business 레이어 포함 복잡 도메인). 권한: `ADMIN`, `STOCK`.

핵심 설계:
- **"전산 재고(expectedQuantity)"** = 해당 상품의 가장 최근 `stock_take.quantity_on_hand` (이전 실사 없으면 `0`). 신규 레코드 생성 시점에 고정 기록된다.
- **Upsert**: `(product, stockTakeDate)` 조합이 이미 존재하면 `quantityOnHand`만 갱신, 없으면 신규 생성(이때 `expectedQuantity` 고정).
- **낙관적 락**: `StockTakeEntity.version`(`@Version`) 추가. 충돌 시 `OptimisticLockingFailureException` → `ApiException(BAD_REQUEST)`로 변환.
- **N+1 방지**: 페이지당 최신 수량 조회 1쿼리(`findLatestByProductIds`, 서브쿼리로 상품별 `MAX(id)` 그룹화) + 오늘 draft 조회 1쿼리.

## 생성/수정된 파일

### 생성
- `src/main/java/com/djccnt15/northwind/db/projection/StockTakeLatestProjection.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/model/StockTakeRowRes.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/model/StockTakeItemReq.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/model/StockTakeSaveReq.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/validation/StockTakeModelConst.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/validation/StockTakeErrorConst.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/converter/StockTakeConverter.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/service/StockTakeService.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/business/StockTakeBusiness.java`
- `src/main/java/com/djccnt15/northwind/domain/stocktake/controller/StockTakeApiController.java`
- `src/test/java/com/djccnt15/northwind/domain/stocktake/business/StockTakeBusinessTest.java`
- `src/test/java/com/djccnt15/northwind/domain/stocktake/service/StockTakeServiceTest.java`
- `src/test/java/com/djccnt15/northwind/domain/stocktake/controller/StockTakeApiControllerTest.java`

### 수정
- `src/main/java/com/djccnt15/northwind/db/entity/StockTakeEntity.java` — `@Version private Long version;` 추가
- `src/main/java/com/djccnt15/northwind/db/repository/StockTakeRepo.java` — 조회/upsert 쿼리 메서드 보강
- `src/main/resources/messages.properties` / `messages_ko.properties` — `validation.stockTake.*` 키 추가
- `src/main/resources/errors.properties` / `errors_ko.properties` — `error.stockTake.*` 키 추가

## API 엔드포인트

베이스 경로: `/api/v1` (인증 필요). 컨트롤러 전체에 `@PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK')")`.

| 메서드 | 경로 | 인증 | 요청 | 응답 바디 (`body`) | 성공 코드 |
|--------|------|------|------|--------------------|----------|
| GET | `/api/v1/stock-takes` | ADMIN 또는 STOCK | query: `page`(기본 0), `size`(기본 20), `keyword`(기본 "") | `Page<StockTakeRowRes>` | 200 |
| POST | `/api/v1/stock-takes` | ADMIN 또는 STOCK | body: `StockTakeSaveReq` (`@Validated`) | `List<StockTakeRowRes>` | 200 |

- 미인증 요청: 401
- 검증 실패(`@NotNull`/`@NotEmpty`/`@PositiveOrZero` 등): 1400, `body`에 `{ field: message }` Map
- 낙관적 락 충돌: 400 (`error.stockTake.conflict` 메시지)
- 존재하지 않는 productId 저장 시: 404 (ProductService가 `error.product.notFound`로 처리)

## 요청 타입 정의

### StockTakeSaveReq (POST body)
```
{
  "stockTakeDate": "2026-06-13",            // LocalDate (ISO yyyy-MM-dd), @NotNull
  "items": [                                 // @NotEmpty @Valid
    { "productId": 1, "quantityOnHand": 35 } // 아래 StockTakeItemReq
  ]
}
```

### StockTakeItemReq
| 필드 | 타입 | 제약 |
|------|------|------|
| `productId` | number (Long) | `@NotNull` |
| `quantityOnHand` | number (Long) | `@NotNull`, `@PositiveOrZero` (0 이상) |

## 응답 타입 정의

### StockTakeRowRes (그리드 1행 / 저장 응답 항목)
| 필드 | 타입 | 설명 |
|------|------|------|
| `productId` | number (Long) | 상품 ID |
| `productCode` | string | 상품 코드 |
| `productName` | string | 상품명 |
| `expectedQuantity` | number (Long) | 전산 재고(예상). 최신 실사 `quantityOnHand`, 이전 실사 없으면 `0`. **항상 non-null** |
| `quantityOnHand` | number (Long) \| **null** | 오늘 작성한 실사 초안 수량. 오늘 draft가 없으면 **null** |

프론트 `StockTakeRowIfs` 대응:
```ts
interface StockTakeRowIfs {
  productId: number;
  productCode: string;
  productName: string;
  expectedQuantity: number;       // non-null
  quantityOnHand: number | null;  // nullable
}
```

- GET 목록은 `Page<StockTakeRowRes>` 형태(`content`, `totalElements`, `number`, `size` 등 표준 Spring Page 필드)로 `body`에 담긴다.
- 목록 대상 상품은 **`discontinued = false`** 인 상품만 포함하며 `id` 오름차순 정렬.
- POST 저장 응답은 저장된 행들의 `List<StockTakeRowRes>` (각 항목의 `expectedQuantity`는 생성 시점에 고정된 값, `quantityOnHand`는 방금 저장한 값).

## i18n 키

messages(.ko):
- `validation.stockTake.date.notNull`
- `validation.stockTake.items.notEmpty`
- `validation.stockTake.product.notNull`
- `validation.stockTake.quantity.notNull`
- `validation.stockTake.quantity.min`

errors(.ko):
- `error.stockTake.productNotFound`
- `error.stockTake.conflict`

## 레이어 책임

- **Controller** (`StockTakeApiController`): 요청/응답 매핑, `@PreAuthorize`, `@Validated` 만.
- **Business** (`StockTakeBusiness`, `@Business` + 저장에 `@Transactional`): `ProductService` + `StockTakeService` + `StockTakeConverter` 조합. 페이지 조회 후 최신/오늘 draft 수량을 묶어 행 구성, 저장 시 item별 upsert.
- **Service** (`StockTakeService`, `@Service`, 단일 리포지토리 `StockTakeRepo` 접근): 최신/draft 수량 Map 조회, upsert 단건 저장 + 낙관적 락 예외 변환.
- **Converter** (`StockTakeConverter`, `@Converter`): `ProductEntity` + expected + draft → `StockTakeRowRes` 변환만.

## 테스트 결과

전체 백엔드 테스트 `BUILD SUCCESSFUL` (worktree 기준 `gradlew -p <worktree> test -x buildFrontend`).

신규 테스트:
- `StockTakeBusinessTest` (`@SpringBootTest @AutoConfigureMockMvc`):
  - 최초 조회 시 `expectedQuantity = 0`, draft `quantityOnHand = null`
  - 저장 후 재조회 시 오늘 draft 반영
  - 같은 날 재저장(upsert): 레코드 1건 유지, 수량 갱신, expected 불변
  - 어제 실사 후 오늘 실사 시 `expectedQuantity`가 직전 `quantityOnHand`로 설정됨
- `StockTakeServiceTest` (`@SpringBootTest @AutoConfigureMockMvc`):
  - upsert 신규 생성 시 expected 0, version 채워짐
  - 같은 날 upsert 수량 갱신/단일 레코드 유지
  - `@Version` 낙관적 락이 stale 버전 저장 시 `OptimisticLockingFailureException` 발생시킴 (서비스의 `upsert`가 동일 예외를 `ApiException(BAD_REQUEST)`로 변환)
- `StockTakeApiControllerTest` (`@WebMvcTest`):
  - 미인증 401
  - STOCK/ADMIN 권한 GET 200, POST 저장 200
  - 검증 실패(날짜 null + items 빈 배열, 음수 수량) 1400

## QA/프론트 유의사항

- `@WebMvcTest` 슬라이스는 메서드 보안(`@PreAuthorize`)을 강제하지 않으므로 컨트롤러 슬라이스 테스트에 "잘못된 권한 403" 케이스는 두지 않았다(기존 `purchase` 컨트롤러 테스트와 동일 관례). 실제 403은 통합 보안 설정에서 적용됨.
- 통합 테스트(`@SpringBootTest`)는 H2 공유 컨텍스트(`DB_CLOSE_DELAY=-1`) 캐시를 위해 반드시 `@SpringBootTest @AutoConfigureMockMvc` 조합을 사용해야 한다(다른 컨텍스트 구성 시 `data-h2.sql` 재실행으로 unique 제약 위반 발생).
- 프론트 `quantityOnHand`는 nullable(number | null) — null이면 입력칸 기본값으로 `expectedQuantity`를 표시(편집 시작점)하는 것을 권장.
- `@Version` 컬럼은 `ddl-auto: update`/H2에서 자동 생성됨(테스트 통과 확인). 운영 MySQL 스키마에는 `stock_take.version BIGINT` 컬럼 추가 필요(마이그레이션 시 참고).
