# QA Report — Phase 1 마스터 데이터 (카테고리 + 상품)

## 검토 결과: PASS

## Critical 수정 사항

### USER 권한 카테고리 조회 403 이슈 (수정 완료)
- **원인**: `products.tsx`와 `product-detail.tsx`가 카테고리 셀렉트 목록을 채우기 위해
  ADMIN 전용 엔드포인트 `GET /v1/admin/categories/all`을 호출. `AdminProductCategoryApiController`는
  클래스 레벨 `@PreAuthorize("hasAnyAuthority('ADMIN')")`이 걸려 있어, USER 권한 로그인 사용자가
  `/products` 또는 `/products/:id`에서 카테고리 필터/편집을 사용하면 403이 발생.
- **수정**:
  - 백엔드: 로그인만 필요한 `GET /api/v1/categories/all` 엔드포인트를 `ProductApiController`에 추가.
    - `@RequestMapping(API_V1 + "/products")` → `@RequestMapping(API_V1)`로 변경하고
      메서드별 매핑을 `/products`, `/products/{id}`, `/categories/all`로 명시.
    - `ProductBusiness`에 `getCategories()` 추가 — `ProductCategoryService.getCategories()`(전체 조회)와
      `ProductCategoryConverter`를 주입해 위임. 기존 admin 비즈니스/컨트롤러는 그대로 유지.
    - `ProductApiController`는 `@PreAuthorize`가 없으므로 URL 기반 보안(`anyRequest().authenticated()`)만
      적용되어 로그인 사용자라면 권한 무관하게 접근 가능. ADMIN 제한 없음 확인.
  - 프론트엔드: `products.tsx`, `product-detail.tsx`의 `fetchCategories` 호출을
    `/v1/admin/categories/all` → `/v1/categories/all`로 변경.
  - 테스트: `ProductApiControllerTest`에 `getAllCategories`(200, @WithMockUser) /
    `getAllCategoriesUnauthorized`(401, 비인증) 2건 추가 — 모두 PASS.

## Major 수정 사항
없음. 백엔드 레이어 컨벤션 전수 검토 결과 위반 없음.

## Minor 수정 사항
없음.

## 백엔드 컨벤션 검토 결과 (전부 PASS)
- `@Transactional(rollbackFor = Exception.class)`은 Business 레이어(`AdminProductCategoryBusiness`,
  `AdminProductBusiness`)에만 존재. 읽기 전용 `ProductBusiness`에는 없음 (적절). Service에는 없음. ✓
- Converter(`ProductConverter`, `ProductCategoryConverter`)는 Entity↔DTO 매핑만 수행, 비즈니스 로직 없음. ✓
- `@Business`, `@Converter` 커스텀 어노테이션 사용. ✓
- 전 클래스 `@RequiredArgsConstructor` + `private final` 의존성 주입. ✓
- 모든 컨트롤러 메서드가 `ResponseEntity<Api<T>>` 반환. ✓
- LIKE 패턴은 Business(`ProductBusiness.getProducts`, `AdminProductCategoryBusiness.getCategories`)에서
  `"%%%s%%".formatted(keyword.trim())`로 생성. ✓
- `ProductRepo.findByFilter`: 본 쿼리에 `JOIN FETCH p.productCategory`, countQuery는 별도 분리
  (`JOIN`, JOIN FETCH 미사용)로 정의. categoryId/discontinued 동적 필터는 `IS NULL OR` 패턴. ✓
- 소프트 딜리트: `AdminProductBusiness.discontinueProduct` → `ProductService.discontinueProduct`에서
  `entity.setDiscontinued(true)` 후 save, 행 유지. ✓
- 카테고리 삭제 시 연관 product 존재하면 BAD_REQUEST:
  `ProductCategoryService.validateCategoryDeletion`에서 `getProductEntitySet().isEmpty()` 검사 후
  `ApiException(BAD_REQUEST, "Category has associated products")`. ✓

## 프론트엔드 컨벤션 검토 결과 (전부 PASS)
- 모든 타입 import가 `import type { ... }` 형식 (verbatimModuleSyntax 준수). ✓
- 도메인 인터페이스 `ProductCategoryIfs`, `ProductIfs`는 `entities/employee.ts`에 정의,
  `entities/index.ts`에서 `export type`로 노출. ✓
  - 참고: `product-detail.tsx`의 `ProductFormState`는 폼 입력 전용 로컬 상태(string 보관) 타입이며
    도메인 모델이 아니므로 컴포넌트 로컬 정의가 적절 (frontend_summary 명시 결정사항).
- API 호출은 `.then().catch().finally()` 체이닝 사용. ✓
- FSD 의존성 방향(pages → features → entities → shared) 준수, 역방향 참조 없음. ✓
- 파일명 kebab-case(`admin-category.tsx`, `products.tsx`, `product-detail.tsx`). ✓
- 이벤트 핸들러 `on~`/`handle~` 접두사(`onKeywordKeyDown`, `handleSearch`, `handleEditClick`,
  `handleSaveClick`, `handleDiscontinueClick`) 준수. ✓
- `useCallback`/`useMemo`: `admin-category.tsx`는 DataGrid CRUD 핸들러 일관성을 위해
  기존 컨벤션(title.tsx) 유지(허용된 결정). `products.tsx`/`product-detail.tsx`는 수동 메모이제이션 없음. ✓

## 경계면 (계약 ↔ 타입) 검토 결과
- `ProductCategoryRes`(id, name, code, description) ↔ `ProductCategoryIfs`(id, code, name, description?)
  필드/타입 일치. ✓
- `ProductRes`(id, code, name, description, standardUnitCost, unitPrice, reorderLevel, targetLevel,
  quantityPerUnit, minimumReorderQuantity, discontinued, category) ↔ `ProductIfs` 전 필드 일치,
  `category: ProductCategoryRes` ↔ `category: ProductCategoryIfs` 일치. ✓
- 응답 래퍼 `ApiIfs<T>` + `PageIfs<T>` 일관 사용, `body.page.totalElements` 접근 경로 일치. ✓
- 인증 엔드포인트는 전부 `privateApi` 사용. ✓

## 테스트 결과
- 전체: 84 passed, 2 failed, 7 skipped (총 86 실행)
- Phase 1 추가/관련 테스트: 전부 PASS (failures="0")
  - ProductApiControllerTest (5건, 신규 categories/all 2건 포함)
  - ProductCategoryServiceTest
  - ProductRepoTest
  - ProductCategoryRepoTest
  - AdminProductCategoryApiControllerTest
- 기존 실패 (무시): `AuthConfigTest.loginSuccess`, `AuthConfigTest.loginFail`
  (Phase 1 범위 밖, forward/CSRF 흐름 문제 — 작업 전부터 동일하게 실패)

## TypeScript 컴파일
PASS — `npx tsc --noEmit` 오류 없음.

## 잔존 이슈
없음. (AuthConfigTest 2건은 Phase 1 범위 밖으로 명시적 제외)
