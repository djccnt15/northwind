# 프론트엔드 구현 요약

상품 관리 권한을 ADMIN 전용에서 로그인 사용자 전체로 변경하고, 신규 상품 생성 UI를 추가했다. 프론트엔드 전용 작업이며 백엔드 계약(요구사항에 제공된 `/api/v1/products` 통합 엔드포인트)을 기준으로 구현했다.

## 생성/수정된 파일

- `frontend/src/pages/product-detail.tsx` (수정) — isAdmin 가드 제거, API 경로 통합, create 모드 추가
- `frontend/src/pages/products.tsx` (수정) — `[+ 신규 상품]` 버튼 추가
- `frontend/src/app/router.tsx` (수정) — `/products/new` 라우트 추가

신규 entities/features 모듈은 추가하지 않았다. 기존 `ProductIfs`, `ProductCategoryIfs`(`entities/employee.ts`) 타입을 그대로 재사용했고, 인터페이스 인라인 정의는 없다. (단, 폼 로컬 상태 타입 `ProductFormState`는 기존 코드부터 페이지 파일 내에 정의되어 있던 것으로, 이번 변경 범위 밖이라 유지함.)

## 추가된 라우트

| 경로 | 페이지 컴포넌트 | 인증 |
|------|----------------|------|
| `/products/new` | `ProductDetail` (create 모드) | 세션 필요 (ProtectedRoute) |
| `/products/:id` | `ProductDetail` (조회/수정 모드) | 세션 필요 (ProtectedRoute) |

`/products/new`를 `/products/:id`보다 먼저 선언하여 `:id`가 `"new"`를 가로채지 않도록 했다. (라우트 우선순위로 1차 방어, 컴포넌트 내 `id === "new"` 체크로 2차 방어.)

## 주요 구현 사항

### product-detail.tsx
- `isAdmin` 가드 및 `useAuth` import 제거 → 로그인 사용자 전체에게 Edit/Discontinue/Save/Cancel 버튼 표시.
- API 경로 통합: PUT/DELETE 모두 `/v1/admin/products/:id` → `/v1/products/:id`.
- create 모드 추가:
  - `isCreateMode = !id || id === "new"`로 판정.
  - create 모드는 처음부터 편집 상태(`isEditing` 초기값 = `isCreateMode`)로 빈 폼 시작, 진입 시 카테고리 목록 fetch.
  - 저장은 `POST /v1/products` 호출, 성공 시 alert 후 `/products`로 navigate.
  - Save 버튼 라벨은 create 모드에서 `Create`, 수정 모드에서 `Save`.
  - create 모드 Cancel은 `/products`로 navigate (수정 모드는 폼 원복).
  - create 모드에서는 편집 상태가 유지되므로 Discontinue 버튼은 렌더되지 않음(읽기 모드 분기에만 존재).
  - 검증 오류(result.code=1400)는 기존 패턴대로 `body`의 field→message Map을 alert로 표시.
- `product`가 null일 수 있는 create 모드를 위해 읽기 분기의 `product.xxx` 참조를 `product?.xxx` 옵셔널 체이닝으로 변경, `if (!isCreateMode && !product)` 가드로 "not found" 오표시 방지.

### products.tsx
- 기존 FilterBar(커스텀 toolbar 영역, 페이지가 QuickToolbar 대신 사용 중) 내 Search 버튼 옆에 `Spacer` + `NewProductBtn`(`+ 신규 상품`) 배치.
- `isAdmin` 조건 없이 모든 로그인 사용자에게 노출, 클릭 시 `navigate("/products/new")`.

### 검증
- 타입 체크(`tsc -p tsconfig.app.json --noEmit`): 통과 (0 errors).
- ESLint(변경 3개 파일): 통과 (0 errors). `react-hooks/set-state-in-effect` 경고를 피하기 위해 create 모드 초기화 setState를 기존 fetch와 동일하게 `queueMicrotask`로 감쌌다.

## QA 주의사항
- 백엔드가 `/api/v1/admin/products` 제거 + `/api/v1/products` POST(ADMIN 불요)를 실제로 제공하는지 확인 필요. 프론트는 계약대로 구현됨.
- create 성공/실패 모두 `window.alert` 사용(기존 페이지 컨벤션 유지).
- 워크트리에 `node_modules`가 없어 검증은 메인 레포의 동일 버전 의존성을 임시 junction으로 연결해 수행 후 junction을 제거함. CI/실제 빌드 시에는 워크트리에서 `npm install` 필요.
