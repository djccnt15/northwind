# 프론트엔드 구현 요약 — 재고 실사 (S-63)

## 생성/수정된 파일

### 생성
- `frontend/src/entities/stock-take.ts` — `StockTakeRowIfs` 타입 정의
- `frontend/src/pages/stock-take.tsx` — 재고 실사 페이지 (서버 페이지네이션 DataGrid + 필터바 + 일괄 저장)

### 수정
- `frontend/src/entities/index.ts` — `StockTakeRowIfs` re-export 추가
- `frontend/src/app/provider/redirect-route.tsx` — `StockRoute`(ADMIN 또는 STOCK) 가드 추가
- `frontend/src/app/router.tsx` — `StockTake` import, `StockRoute`로 감싼 `/stock-take` 라우트 추가
- `frontend/src/widgets/navbar-left.tsx` — "상품/재고" 영역에 "재고 실사" 링크 추가 (ADMIN 또는 STOCK 노출)
- `frontend/CLAUDE.md` — 라우트 가드 표에 `StockRoute` 행 추가

## 추가된 라우트

| 경로 | 페이지 컴포넌트 | 인증 |
|------|--------------|------|
| `/stock-take` | `StockTake` (`pages/stock-take.tsx`) | `StockRoute` (ADMIN 또는 STOCK) |

## 주요 구현 사항

### 타입 (`StockTakeRowIfs`)
백엔드 계약(`02_backend_contract.md`)의 `StockTakeRowRes`와 1:1 대응:
```ts
interface StockTakeRowIfs {
  productId: number;
  productCode: string;
  productName: string;
  expectedQuantity: number;       // non-null
  quantityOnHand: number | null;  // 오늘 draft 없으면 null
}
```
DataGrid `getRowId`는 `productId`를 사용한다(행에 `id` 필드 없음).

### 페이지 (`stock-take.tsx`)
- `products.tsx`의 필터바 + 서버 페이지네이션 DataGrid 패턴 차용 (`paginationMode="server"`, `queueMicrotask`로 페이지 변경 시 `GET /v1/stock-takes?page=&size=&keyword=` 호출).
- 컬럼: 상품코드 / 상품명 / 전산재고(`expectedQuantity`, 읽기전용) / 실사재고(`<input type=number>` 편집) / 차이(계산값, 0이 아니면 색상+볼드 강조 — 양수 초록/음수 빨강).
- **편집 버퍼**: `edits: Map<productId, number>` 로컬 state로 행별 실사 입력값 관리. 입력칸 표시값 우선순위 = 편집값 → `quantityOnHand`(오늘 draft) → `expectedQuantity`(전산재고, 편집 시작점). 계약의 "null이면 expectedQuantity를 기본값으로" 권고를 따름.
- **변경된 행만 전송**: 현재 입력값이 기존 `quantityOnHand`와 다른 행만 `items`로 모아 `POST /v1/stock-takes` (`{ stockTakeDate: 오늘(YYYY-MM-DD, 로컬 시간 기준), items: [{ productId, quantityOnHand }] }`). 변경 건수를 저장 버튼 라벨에 표시하고, 0건이면 버튼 비활성화.
- **저장 응답 처리**: 응답 `List<StockTakeRowRes>`로 해당 행을 갱신하고 저장된 productId의 편집 버퍼 제거. 성공 메시지 표시.
- **에러 처리**: `1400`(검증 오류)이면 `body`의 `{ field: message }` Map 값을 합쳐 표시, 그 외(400 낙관적 락 충돌 등)는 `result.description` 표시. 메시지 바는 에러/성공 색상 구분.
- 페이지 이동·재조회 시 편집 버퍼를 초기화하여 다른 페이지의 미저장 입력이 잘못 전송되는 것을 방지.

### FSD / 컨벤션 준수
- 타입은 `entities/`에만 정의(인라인 인터페이스 없음), `~Ifs` 접미사.
- API 호출은 `privateApi` + `.then().catch().finally()` 체이닝.
- 타입 전용 import는 `import type` 사용(`verbatimModuleSyntax`).
- React Compiler 활성화 환경이라 `useMemo`/`useCallback` 수동 작성 없음.
- 레이어 방향 준수: `app(router/guard) → pages → entities/shared`.

## QA 주의사항
- 백엔드 `quantityOnHand`가 `null`(오늘 draft 없음)인 행은 입력칸이 `expectedQuantity`로 채워진다. 사용자가 값을 바꾸지 않으면 "변경 없음"으로 간주되어 전송 대상에서 제외된다(`edited === undefined`). 즉, 전산재고와 동일한 값을 "확정 저장"하려면 사용자가 한 번 값을 입력/수정해야 한다 — 이는 변경분만 일괄 전송하는 설계 의도에 따름.
- `quantityPerUnit` 등 product의 다른 수량 단위와 무관하게, 전산재고/실사재고/차이는 모두 `stock_take.quantity_on_hand` 단위(개수) 기준.
- 빌드 검증: worktree에 `frontend/node_modules`가 없어 `npm install` 후 `npm run build`(`tsc -b && vite build`) 통과 확인. (출력: `../src/main/resources/static/`)

## 빌드 결과
`cd frontend; npm run build` → `tsc -b` 타입 체크 통과 + `vite build` 번들링 성공 (`✓ built`). 경고는 기존 청크 크기/`eval`(MUI exceljs) 관련으로 본 변경과 무관.
