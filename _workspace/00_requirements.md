# 요구사항

## 기능 설명
백엔드 권한 체계 변경(commit 73b9be66)에 맞게 프론트엔드 라우트 가드와 내비게이션 바를 업데이트한다.

## 범위: 프론트엔드 전용

## 참고 도메인 (유사한 기존 구현)
- `frontend/src/app/provider/redirect-route.tsx` — 기존 라우트 가드 (수정 대상)
- `frontend/src/app/router.tsx` — 라우터 구성 (수정 대상)
- `frontend/src/widgets/navbar-left.tsx` — 내비게이션 바 (수정 대상)

## 특이사항

### 백엔드 변경 내용 (commit 73b9be66)
- `ProductApiController`: `@PreAuthorize("hasAnyAuthority('PRODUCT')")` 추가
- `AdminProductCategoryApiController`: `ADMIN` → `ADMIN, MANAGER` 로 확장
- 신규 역할 상수: `COMPANY`, `ORDER`, `PURCHASE`, `PRODUCT`, `STOCK`

### StoryBoard 권한 기준
| 화면 | 경로 | 필요 권한 |
|------|------|----------|
| S-60 상품 목록 | `/products` | ADMIN, PRODUCT |
| S-61 상품 상세 | `/products/:id` | ADMIN, PRODUCT |
| S-62 카테고리 관리 | `/admin/categories` | ADMIN, MANAGER |

### 프론트엔드 변경 목록
1. `redirect-route.tsx`
   - `ProductRoute` 추가: `ADMIN` 또는 `PRODUCT` 권한 없으면 `/home` 리다이렉트
   - `ManagerRoute` 추가: `ADMIN` 또는 `MANAGER` 권한 없으면 `/home` 리다이렉트

2. `router.tsx`
   - `/products`, `/products/new`, `/products/:id` → `ProtectedRoute` → `ProductRoute` 그룹으로 이동
   - `/admin/categories` → `AdminRoute` 그룹 → 별도 `ManagerRoute` 그룹으로 분리

3. `navbar-left.tsx`
   - Products 링크: `ADMIN` 또는 `PRODUCT` 권한 있는 사용자만 표시
   - Admin - Category 링크: `ADMIN` 또는 `MANAGER` 권한 있는 사용자만 표시
   - Admin - User / Title / Team / OpenAPI: 기존 ADMIN only 유지

## Worktree
- 브랜치: feature/frontend-permission-routes
- 경로: C:/projects/northwind/.worktree/feature/frontend-permission-routes
