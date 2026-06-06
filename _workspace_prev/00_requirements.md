# 요구사항

## 기능 설명
상품 관리 권한을 ADMIN 전용에서 로그인 사용자 전체로 변경하고, 신규 상품 생성 기능을 추가한다.

## 범위: 풀스택

## 참고 도메인 (유사한 기존 구현)
- `domain/product/` — 기존 상품 도메인 (수정 대상)
- `domain/admin/` — 기존 admin 상품 비즈니스/컨트롤러 (제거 대상)
- `domain/title/` — 일반 사용자 CRUD 패턴 참고

## 특이사항

### 백엔드
- `ProductBusiness`에 `createProduct`, `updateProduct`, `discontinueProduct` 추가 (`@Transactional`)
- `ProductApiController`에 POST/PUT/DELETE 엔드포인트 추가 (`@PreAuthorize` 없음, 로그인만 필요)
- `AdminProductBusiness`, `AdminProductApiController` 제거 (상품 관련 내용이 없어지면 파일 삭제)
- 기존 `ProductService`에 create/update/discontinue가 이미 구현되어 있으므로 Business 레이어만 추가하면 됨

### 프론트엔드
- `product-detail.tsx`: `isAdmin` 가드 제거, API 경로 `/v1/admin/products` → `/v1/products`, create 모드 추가 (id 없을 때 빈 폼 + POST)
- `products.tsx`: `[+ 신규 상품]` 버튼 추가 (isAdmin 없음), 클릭 시 `/products/new` navigate
- `router.tsx`: `/products/new` 라우트 추가 (`:id` 경로보다 먼저 선언)

## Worktree
- 브랜치: feature/product-user-management
- 경로: C:/projects/northwind/.worktree/feature/product-user-management
