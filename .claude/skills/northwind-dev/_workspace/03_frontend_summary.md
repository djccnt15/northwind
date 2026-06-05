# Frontend Summary — Phase 1 마스터 데이터

## 생성/수정된 파일

### 신규
- frontend/src/pages/admin-category.tsx — S-62 ADMIN 카테고리 CRUD (title.tsx DataGrid 패턴)
- frontend/src/pages/products.tsx — S-60 로그인 사용자 상품 목록 (필터 + 서버 페이지네이션)
- frontend/src/pages/product-detail.tsx — S-61 상품 상세 (읽기 전용 / ADMIN 편집·소프트딜리트)

### 수정
- frontend/src/entities/employee.ts — `ProductCategoryIfs`, `ProductIfs` 타입 추가
- frontend/src/entities/index.ts — 신규 타입 export 추가
- frontend/src/app/router.tsx — 라우트 3개 추가
- frontend/src/widgets/navbar-left.tsx — Products(로그인) / Admin - Category(ADMIN) 링크 추가

## 추가된 라우트

| 경로 | 페이지 컴포넌트 | 인증 |
|------|----------------|------|
| /products | Products | ProtectedRoute (로그인) |
| /products/:id | ProductDetail | ProtectedRoute (로그인) |
| /admin/categories | AdminCategory | AdminRoute (ADMIN) |

## TypeScript 컴파일 결과
PASS — `npx tsc --noEmit` 오류 없음.
추가로 `npx eslint`(react-hooks 포함) 변경 파일 전체 CLEAN.

## 주요 구현 사항

- **admin-category.tsx**: title.tsx/admin-team.tsx의 DataGrid editMode="row" CRUD 패턴을 그대로 복제.
  컬럼 code/name/description(editable), API `/v1/admin/categories`, 요청 body `{ code, name, description }`,
  신규 행 초기값 `{ id, code:"", name:"", description:"", isNew:true }`, fieldToFocus="code".
  `onEdit`에서 `isNew`를 구조분해로 제거 후 전송(admin-team.tsx와 동일).

- **products.tsx**: 읽기 전용 DataGrid(editMode 없음). keyword/categoryId/discontinued 동적 필터.
  필터값은 빈 문자열이면 params에서 제외(`if (... !== "")`). Enter 키 또는 Search 버튼으로 조회.
  `discontinued` 셀렉트는 문자열("true"/"false")을 boolean으로 변환. Name 컬럼 클릭 시 `/products/:id` 이동.
  category 셀렉트박스는 `/v1/admin/categories/all`로 로딩.
  주의: `/v1/admin/categories/all`은 백엔드 계약상 ADMIN 권한 엔드포인트. USER 계정에서는 403이 날 수 있어
  카테고리 필터 목록이 비게 될 가능성이 있음(에러는 console.error로만 처리, 페이지 동작은 유지). QA 시 확인 필요.

- **product-detail.tsx**: `ProductFormState`를 로컬 인터페이스로 선언(폼 입력은 string으로 보관, 저장 시 Number 변환).
  `useAuth().user.authorities.includes("ADMIN")`로 ADMIN 여부 판정. 비ADMIN은 읽기 전용.
  Edit → `PUT /v1/admin/products/:id`, Cancel → 폼 복원, Discontinue → confirm 후 `DELETE /v1/admin/products/:id`(소프트딜리트) 후 재조회.
  `discontinued=true`면 헤더에 "판매중단" 뱃지. 이미 discontinued면 Discontinue 버튼 비활성화.
  검증 오류(code 1400) 시 body의 field 메시지를 alert로 표시.

## 의존 관계 / QA 주의사항

- React Compiler 활성화로 useMemo/useCallback 수동 작성 불필요하나, DataGrid CRUD 페이지는 기존
  컨벤션(title.tsx)을 유지해 useCallback/useMemo를 그대로 사용(rowModesModel 핸들러 패턴 일관성).
- `react-hooks/set-state-in-effect` 규칙 대응: 데이터 조회 effect는 기존 페이지와 동일하게
  `queueMicrotask(() => fetch...)`로 감싸 동기 setState 경고를 회피.
- 통화 표시는 `Number(value).toFixed(2)`로 `$` prefix 포맷(목록/상세 일관).
