# 요구사항

## 기능 설명
Northwind ERP Phase 1 — 마스터 데이터 (카테고리 + 상품) 구현

- S-62: `/admin/categories` — ADMIN 전용 카테고리 CRUD (title/team과 동일한 DataGrid CRUD 패턴)
- S-60: `/products` — 로그인 사용자 상품 목록 (keyword/category/discontinued 필터 + 서버 페이지네이션)
- S-61: `/products/:id` — 상품 상세 (읽기 전용 기본, ADMIN은 편집/소프트딜리트 가능)

## 범위: 풀스택

## 참고 도메인 (유사한 기존 구현)
- 백엔드: `domain/title`, `domain/team`, `domain/admin` (AdminTitleBusiness, AdminTeamBusiness 패턴)
- 프론트엔드: `pages/title.tsx`, `pages/admin-team.tsx` (DataGrid CRUD 패턴)

## DB Entity 현황
- `ProductCategoryEntity` — 이미 완성, `cascade=REMOVE` (연관 product 삭제됨)
- `ProductEntity` — 이미 완성, `productCategory` ManyToOne LAZY
- `ProductCategoryRepo`, `ProductRepo` — 비어있음 (쿼리 메서드 추가 필요)

## 특이사항
- Product 삭제는 소프트 딜리트 (`discontinued=true`), 물리 삭제 없음
- ProductRepo는 JOIN FETCH + 동적 필터(@Query) 필요 — N+1 방지
- countQuery는 JOIN FETCH 없이 분리 (Spring Data JPA 제약)
- 카테고리 삭제 시 연관 product 존재하면 BAD_REQUEST 반환 (LAZY 접근이므로 @Transactional 경계 안)
- 프론트엔드 React Compiler 활성화 — useCallback/useMemo 수동 작성 불필요
- TypeScript verbatimModuleSyntax — 타입은 반드시 `import type` 사용
