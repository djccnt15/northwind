# 요구사항

## 기능 설명

`doc/StoryBoard.md` S-63 "재고 실사" 화면 풀스택 구현.

- 경로: `/stock-take`
- 접근 권한: `ADMIN`, `STOCK`
- 목적: 전산상 재고(이전 실사 결과)와 실제 창고 재고(이번 실사 입력값)를 비교·조정
- 화면 구성: 상품별 행을 가진 그리드. 컬럼 = 상품명, 전산 재고(예상), 실사 재고(실제, 입력), 차이(자동 계산/강조)
- 저장 시 입력된 실사 재고를 일괄 반영(batch save)

## 범위: 풀스택

## 참고 도메인 (유사한 기존 구현)

- **엔티티/리포지토리 스텁 존재**: `db/entity/StockTakeEntity.java`, `db/repository/StockTakeRepo.java` (둘 다 최소 골격만 있고 도메인 레이어 `domain/stocktake/*` 없음 — 이번 작업에서 신설)
- **Product 도메인** (`domain/product/*`): 페이지네이션 목록 조회(`ProductRepo.findByFilter`), `ProductEntity` (재고 관련 필드: `reorderLevel`, `targetLevel`, `minimumReorderQuantity` — **`unitsInStock`류 "현재 재고" 컬럼은 product 테이블에 없음**, ERD상 재고 현황은 오직 `stock_take.quantity_on_hand`로만 추적됨)
- **프론트 상품 목록** (`pages/products.tsx`): DataGrid + 필터바 패턴, 서버 페이지네이션
- **DataGrid 인라인 편집 CRUD** (`pages/admin-category.tsx`): `ActionHandlersContext`, `processRowUpdate` 패턴
- **라우트 가드** (`app/provider/redirect-route.tsx`): `ProductRoute`/`OrderRoute`/`PurchaseRoute`/`CompanyRoute`/`ManagerRoute` 패턴 — 이번 작업에서 `StockRoute`(ADMIN, STOCK) 신설

## 특이사항

- **"전산 재고(예상)" 정의**: `product` 테이블에 현재 재고 컬럼이 없으므로, 특정 상품의 "전산 재고"는 **해당 상품의 가장 최근 `stock_take.quantity_on_hand`** 값으로 정의한다 (최초 실사인 경우 0). 이번 실사에서 입력하는 `quantity_on_hand`가 다음 실사의 "전산 재고" 기준이 된다.
- **동시성 제어**: PRD 기술 제약사항에 따라 `StockTakeEntity`에 `@Version`(낙관적 락) 컬럼을 추가한다. 동일 (product, stock_take_date) 조합에 대한 동시 저장 시 충돌을 감지한다.
- **Upsert 정책**: 같은 날 같은 상품에 대해 이미 실사 레코드가 존재하면(같은 세션 재저장/이어하기) 해당 레코드를 갱신(`quantityOnHand`)하고, 없으면 신규 생성한다. `expectedQuantity`는 신규 생성 시점에 "그 이전까지의 최신 `quantity_on_hand`"로 고정 기록한다.
- 기존 도메인 디렉토리 오타 수정 작업(`puchase`→`purchase`)은 이미 별도 작업(e40c092)에서 완료됨 — 이번 작업과 무관.

## Worktree
- 브랜치: feature/stock-take
- 경로: C:/projects/northwind/.worktree/feature/stock-take
