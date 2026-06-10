# 백엔드 API 계약 — Phase 1 마스터 데이터 (카테고리 + 상품)

## 생성/수정된 파일

### 신규 (main)
- src/main/java/com/djccnt15/northwind/domain/product/model/ProductCategoryCreateReq.java
- src/main/java/com/djccnt15/northwind/domain/product/model/ProductCategoryRes.java
- src/main/java/com/djccnt15/northwind/domain/product/converter/ProductCategoryConverter.java
- src/main/java/com/djccnt15/northwind/domain/product/service/ProductCategoryService.java
- src/main/java/com/djccnt15/northwind/domain/product/model/ProductCreateReq.java
- src/main/java/com/djccnt15/northwind/domain/product/model/ProductRes.java
- src/main/java/com/djccnt15/northwind/domain/product/converter/ProductConverter.java
- src/main/java/com/djccnt15/northwind/domain/product/service/ProductService.java
- src/main/java/com/djccnt15/northwind/domain/product/business/ProductBusiness.java
- src/main/java/com/djccnt15/northwind/domain/product/controller/ProductApiController.java
- src/main/java/com/djccnt15/northwind/domain/admin/business/AdminProductCategoryBusiness.java
- src/main/java/com/djccnt15/northwind/domain/admin/business/AdminProductBusiness.java
- src/main/java/com/djccnt15/northwind/domain/admin/controller/AdminProductCategoryApiController.java
- src/main/java/com/djccnt15/northwind/domain/admin/controller/AdminProductApiController.java

### 수정 (main)
- src/main/java/com/djccnt15/northwind/db/repository/ProductCategoryRepo.java
- src/main/java/com/djccnt15/northwind/db/repository/ProductRepo.java
- src/main/java/com/djccnt15/northwind/domain/product/validation/ProductCategoryModelConst.java
- src/main/java/com/djccnt15/northwind/domain/product/validation/ProductModelConst.java

### 신규 (test)
- src/test/java/com/djccnt15/northwind/db/repository/ProductCategoryRepoTest.java
- src/test/java/com/djccnt15/northwind/db/repository/ProductRepoTest.java
- src/test/java/com/djccnt15/northwind/domain/product/service/ProductCategoryServiceTest.java
- src/test/java/com/djccnt15/northwind/domain/admin/controller/AdminProductCategoryApiControllerTest.java
- src/test/java/com/djccnt15/northwind/domain/product/controller/ProductApiControllerTest.java

### 수정 (test)
- src/test/resources/data-h2.sql (Beverages 카테고리 + Chai 상품 시드 추가)

## API 엔드포인트

| 메서드 | 경로 | 인증 | 요청 바디 | 응답 바디 |
|--------|------|------|----------|----------|
| POST   | /api/v1/admin/categories | ADMIN | ProductCategoryCreateReq | ProductCategoryRes (201) |
| GET    | /api/v1/admin/categories?page=0&size=10&keyword= | ADMIN | - | Page&lt;ProductCategoryRes&gt; |
| GET    | /api/v1/admin/categories/all | ADMIN | - | List&lt;ProductCategoryRes&gt; |
| PUT    | /api/v1/admin/categories/{id} | ADMIN | ProductCategoryCreateReq | ProductCategoryRes |
| DELETE | /api/v1/admin/categories/{id} | ADMIN | - | null |
| GET    | /api/v1/products?page=0&size=10&keyword=&categoryId=&discontinued= | 로그인 | - | Page&lt;ProductRes&gt; |
| GET    | /api/v1/products/{id} | 로그인 | - | ProductRes |
| POST   | /api/v1/admin/products | ADMIN | ProductCreateReq | ProductRes (201) |
| PUT    | /api/v1/admin/products/{id} | ADMIN | ProductCreateReq | ProductRes |
| DELETE | /api/v1/admin/products/{id} | ADMIN | - | null (소프트 딜리트: discontinued=true) |

모든 응답은 공통 래퍼 `Api<T>` { serverTime, result{code,message,description}, body } 형태.
- 성공 200 → result.code=200
- 생성 → result.code=201
- 검증 오류 → result.code=1400, body={ field: message }
- 비인증 → HTTP 401, 권한없음 → HTTP 403

## 요청 타입 정의

### ProductCategoryCreateReq
| 필드 | 타입 | 제약 |
|------|------|------|
| name | string | NotBlank, 1~50자, unique |
| code | string | NotBlank, 1~20자, unique |
| description | string | optional |

### ProductCreateReq
| 필드 | 타입 | 제약 |
|------|------|------|
| code | string | NotBlank, 1~50자, unique |
| name | string | NotBlank, 1~255자, unique |
| description | string | optional |
| standardUnitCost | number (BigDecimal) | NotNull |
| unitPrice | number (BigDecimal) | NotNull |
| reorderLevel | integer | NotNull |
| targetLevel | integer | NotNull |
| quantityPerUnit | integer | NotNull |
| minimumReorderQuantity | integer | NotNull |
| discontinued | boolean | NotNull |
| categoryId | number (Long) | NotNull, 존재하는 카테고리 ID |

## 응답 타입 정의

### ProductCategoryRes
| 필드 | 타입 |
|------|------|
| id | number (Long) |
| name | string |
| code | string |
| description | string \| null |

### ProductRes
| 필드 | 타입 |
|------|------|
| id | number (Long) |
| code | string |
| name | string |
| description | string \| null |
| standardUnitCost | number (BigDecimal) |
| unitPrice | number (BigDecimal) |
| reorderLevel | integer |
| targetLevel | integer |
| quantityPerUnit | integer |
| minimumReorderQuantity | integer |
| discontinued | boolean |
| category | ProductCategoryRes |

### Page<T> (Spring Data 기본 직렬화)
`body` 안에 `content: T[]`, `totalElements`, `totalPages`, `number`, `size`, `first`, `last`, `empty` 등 포함.

## 비즈니스 규칙
- 카테고리/상품 생성·수정 시 name, code 중복 검증 (BAD_REQUEST 400).
- 카테고리 삭제 시 연관 상품이 있으면 거부 ("Category has associated products", 400). 없으면 삭제 (cascade REMOVE).
- 상품 삭제는 소프트 딜리트(discontinued=true)이며 행은 유지됨.
- 상품 목록은 keyword(name/code LIKE) + categoryId + discontinued 동적 필터, JOIN FETCH로 카테고리 N+1 방지.
- 상품 상세는 @EntityGraph로 카테고리 즉시 로딩.

## 레이어 구성
- 카테고리: Controller(Admin) → Business(AdminProductCategoryBusiness, @Transactional) → Service(ProductCategoryService) → Repo
- 상품 읽기: Controller(ProductApiController) → Business(ProductBusiness) → Service → Repo
- 상품 쓰기: Controller(AdminProductApiController) → Business(AdminProductBusiness, @Transactional, 두 Service 조합) → Service → Repo

## 테스트 결과

신규 작성한 모든 테스트 PASS:
- ProductCategoryRepoTest (uniqueName, uniqueCode, existsByName/Code(AndIdNot), findByNameLike)
- ProductRepoTest (findByFilter: keyword/categoryId/discontinued, findWithCategoryById)
- ProductCategoryServiceTest (getCategory, validateCategory(생성/수정), CRUD, validateCategoryDeletion)
- AdminProductCategoryApiControllerTest (@WebMvcTest: 401 비인증, 403 USER, 200 ADMIN — @EnableMethodSecurity 적용)
- ProductApiControllerTest (@WebMvcTest: 401 비인증, 200 로그인 목록/상세)

명령: `.\gradlew.bat test --tests "com.djccnt15.northwind.domain.product.*" --tests "com.djccnt15.northwind.db.repository.ProductRepoTest" --tests "com.djccnt15.northwind.db.repository.ProductCategoryRepoTest" --tests "com.djccnt15.northwind.domain.admin.*"`
→ BUILD SUCCESSFUL

### 참고: 본 작업과 무관한 기존 실패
- `AuthConfigTest.loginSuccess`, `AuthConfigTest.loginFail` 2건은 작업 시작 전 워킹트리 상태에서도 동일하게 실패(로그인 forward/CSRF 흐름 문제, line 41 IllegalArgumentException = forwardedUrl null). 본 Phase 1 마스터 데이터 구현과 무관하며 인증 코드/테스트는 수정하지 않음.

## 구현 결정 사항 (계획 대비 차이)
- `ProductModelConst.NAME_MAX_LENGTH`는 계획의 100 대신 기존 값 255를 유지. ProductEntity의 name 컬럼 길이가 255로 정의되어 있고 `ddl-auto: validate`가 적용되므로, 100으로 줄이면 검증 메시지와 컬럼 정의 간 불일치 및 스키마 검증 위험이 발생. 엔티티 컬럼 길이와 @Size 검증을 동일 상수(255)로 정합성 유지.
