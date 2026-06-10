# 프론트엔드 구현 요약

## 생성/수정된 파일
- frontend/src/app/provider/redirect-route.tsx (가드 추가: `ProductRoute`, `ManagerRoute`)
- frontend/src/app/router.tsx (라우트 그룹 재구성)
- frontend/src/widgets/navbar-left.tsx (내비게이션 조건부 표시 변경)

> 신규 파일/타입 정의는 없음. 기존 가드 패턴(`ChildNodeIfs`, `useAuth`)을 재사용하여 인라인 타입 정의 없이 구현.

## 추가된 라우트 가드 (redirect-route.tsx)

| 가드 | 조건 | 리다이렉트 |
|------|------|-----------|
| `ProductRoute` | `ADMIN` 또는 `PRODUCT` 권한 없음 | `/home` |
| `ManagerRoute` | `ADMIN` 또는 `MANAGER` 권한 없음 | `/home` |

- 기존 `AdminRoute` 패턴과 동일하게 `!user || !(...)` 형태로 작성하여 비로그인 사용자도 안전하게 처리.

## 라우트 구성 변경 (router.tsx)

| 경로 | 페이지 컴포넌트 | 가드 (인증) |
|------|--------------|------------|
| `/products` | `Products` | `ProductRoute` (ADMIN/PRODUCT) |
| `/products/new` | `ProductDetail` | `ProductRoute` (ADMIN/PRODUCT) |
| `/products/:id` | `ProductDetail` | `ProductRoute` (ADMIN/PRODUCT) |
| `/admin/categories` | `AdminCategory` | `ManagerRoute` (ADMIN/MANAGER) |
| `/admin/user` `/admin/titles` `/admin/team` | (각 페이지) | `AdminRoute` (ADMIN) — 유지 |
| `/home` `/profile` | `Home` / `Profile` | `ProtectedRoute` (로그인) — 유지 |

- `/products*` 3개 라우트를 `ProtectedRoute` 그룹에서 분리하여 별도 `ProductRoute` 그룹(`path="/"`)으로 이동.
- `/admin/categories`를 `AdminRoute` 그룹에서 꺼내 별도 `ManagerRoute` 그룹(`path="/admin"`)으로 분리. 나머지 admin 라우트는 `AdminRoute` 유지.

## 내비게이션 변경 (navbar-left.tsx)

| 메뉴 | 표시 조건 (변경 후) |
|------|--------------------|
| Products | `ADMIN \|\| PRODUCT` |
| Admin - Category | `ADMIN \|\| MANAGER` (별도 `NavList` 블록으로 분리) |
| Admin - User / Title / Team | `ADMIN` 전용 (유지) |
| OpenAPI (Swagger) | `ADMIN` 전용 (유지) |

## 주요 구현 사항 / QA 주의사항
- FSD 레이어 규칙 준수: `app`(router, redirect-route) → `widgets`(navbar-left) → `features`(useAuth) 방향만 참조. 역방향 없음.
- 권한 비교는 `user?.authorities.includes(...)` 패턴을 그대로 사용 (기존 navbar 컨벤션과 일치).
- React Compiler 활성화 환경이므로 수동 메모이제이션 없음.
- TypeScript strict 빌드(`tsc -b`) 통과 — 미사용 변수/파라미터, `import type` 위반 없음. (worktree에 node_modules 설치 후 검증, EXIT=0)
- 백엔드 권한 매핑 일치 확인:
  - `ProductApiController` `hasAnyAuthority('PRODUCT')` + ADMIN → 프론트 `ProductRoute`와 일치.
  - `AdminProductCategoryApiController` ADMIN/MANAGER → 프론트 `ManagerRoute`와 일치.
- QA 시 권한별 시나리오 확인 권장: (1) PRODUCT 권한만 보유 사용자가 `/products` 접근 가능 + `/admin/*` 차단, (2) MANAGER 권한 사용자가 `/admin/categories`만 접근 가능 + 다른 admin 메뉴 비표시, (3) 일반 로그인 사용자는 Products/Category 메뉴 모두 비표시 및 직접 URL 접근 시 `/home` 리다이렉트.
