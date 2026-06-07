# 프론트엔드 구현 요약 — 거래처(Company) 도메인

S-30 거래처 목록 / S-31 거래처 상세 / S-32 담당자 관리 패널을 FSD 레이어 규칙에 따라 구현.

## 생성/수정된 파일

### 신규 (entities)
- frontend/src/entities/company.ts — `CompanyTypeIfs`, `TaxStatusIfs`, `CompanyIfs`, `ContactIfs`, `OrderSummaryIfs`, `PurchaseOrderSummaryIfs` (백엔드 계약과 1:1, nullable 필드는 `string | null`)

### 신규 (features)
- frontend/src/features/company/contact-panel.tsx — S-32 담당자 인라인 CRUD 패널 (companyId prop 받아 자체 목록 상태/API 관리)
- frontend/src/features/company/company-create-modal.tsx — 신규 거래처 등록 모달
- frontend/src/features/company/index.ts — feature public API (`ContactPanel`, `CompanyCreateModal`)

### 신규 (pages)
- frontend/src/pages/companies.tsx — S-30 거래처 목록
- frontend/src/pages/company-detail.tsx — S-31 거래처 상세 + S-32 담당자 패널 + 주문/발주 이력

### 수정
- frontend/src/entities/index.ts — company 타입 6종 export 추가
- frontend/src/app/router.tsx — `/companies`, `/companies/:id` 라우트 추가 (ProtectedRoute 그룹)
- frontend/src/widgets/navbar-left.tsx — "거래처" 메뉴 추가 (로그인 사용자 전체 노출)

## 추가된 라우트

| 경로 | 페이지 컴포넌트 | 인증 |
|------|----------------|------|
| /companies | Companies | ProtectedRoute (로그인) |
| /companies/:id | CompanyDetail | ProtectedRoute (로그인) |

요구사항대로 별도 권한 체크 없이 로그인만 확인. 네비 메뉴도 모든 로그인 사용자에게 노출.

## 백엔드 계약 매핑 (검증 완료)

컨트롤러 소스(`domain/company/controller`, `domain/tax`)와 대조하여 13개 엔드포인트 전부 일치 확인:
- 목록 `GET /api/v1/companies` 쿼리 파라미터: `page`(기본0), `size`(기본20), `type`(**Long = 회사유형 ID**), `keyword`(기본"")
- 요청 바디 필드명(`CompanyCreateReq`, `ContactCreateReq`)이 프론트 전송 키와 정확히 일치 (companyTypeId, taxStatusId 포함)

## 주요 구현 사항

- **companies.tsx (S-30)**
  - company_type 탭 필터: `GET /v1/company-types`로 탭 목록 로딩. **탭 값은 type 이름이 아니라 type ID**를 사용 (백엔드 `@RequestParam Long type`에 맞춤). "전체" 탭은 `type` 파라미터 생략.
  - 검색창: 400ms 디바운스(`setTimeout` cleanup). 디바운스된 값/탭이 바뀌면 첫 페이지로 리셋 후 조회. 최초 마운트 1회는 `isFirstFilterRun` ref로 중복 조회 방지.
  - 서버 사이드 페이지네이션: products.tsx와 동일한 `paginationMode="server"` 패턴, `dataGridInitialState` 재사용.
  - 컬럼: 거래처명(클릭 시 `/companies/:id` 이동), 유형, 전화번호, 도시, 과세유형. (목록 API `CompanyRes`에 담당자 정보가 없어 담당자 컬럼은 제외.)
  - 신규 등록: `CompanyCreateModal` 모달. 생성 성공 시 상세 페이지로 이동.

- **company-detail.tsx (S-31)**
  - 헤더: 거래처명 + 유형 뱃지(파랑) + 과세유형 뱃지(회색), "← 목록으로" 버튼.
  - 기본 정보 카드: 읽기/수정 토글 폼. 수정 진입 시 `GET /v1/company-types`, `GET /v1/tax-statuses`로 셀렉트 옵션 로딩. 저장 `PUT /v1/companies/:id`, 삭제 `DELETE /v1/companies/:id`(확인 후 목록 이동).
  - 담당자 카드: `ContactPanel` feature 임베드.
  - 주문/발주 이력: 유형 라벨 휴리스틱(고객/customer → 주문, 공급/supplier/vendor → 발주) **또는** 해당 이력 데이터가 1건이라도 있으면 섹션 표시. 마운트 시 두 API를 모두 호출해 두고 조건부 렌더.

- **contact-panel.tsx (S-32)**
  - `editingId` 상태(`number | "new" | null`)로 추가 폼 / 특정 행 인라인 수정 / 비편집을 구분.
  - CRUD: `GET/POST/PUT/DELETE /v1/companies/:id/contacts[/:contactId]`. 저장 후 목록 재조회.
  - firstName/lastName 필수 검증, 빈 문자열은 `convertEmptyStringToNull`로 null 변환.

- **company-create-modal.tsx**
  - 셀렉트 기본값(첫 옵션)은 effect에서 setState하지 않고 **렌더 시 파생값**(`selectedCompanyTypeId`/`selectedTaxStatusId`)으로 처리 → `react-hooks/set-state-in-effect` 위반 회피.
  - Escape 키로 닫기(`useKeyDown`), 오버레이 클릭 닫기, 폼 내부 클릭은 `stopPropagation`.

## 컨벤션 준수

- 모든 인터페이스는 `entities/`에만 정의(`~Ifs` 접미사). 컴포넌트 파일 내 도메인 타입 인라인 정의 없음(폼 로컬 상태 타입 `*FormState`만 컴포넌트 파일에 한정).
- API 호출은 `privateApi` + `.then().catch().finally()` 체이닝. 공통 응답 래퍼 `ApiIfs<T>` / `PageIfs<T>` 사용.
- styled-components + `shared/ui/global-styles` 헬퍼(commBtnSkyBlue, commBorderRadius 등) 재사용.
- 데이터 조회 effect는 기존 페이지와 동일하게 `queueMicrotask`로 감싸 set-state-in-effect 경고 회피.
- FSD 의존 방향(pages → features → entities → shared) 준수. 역방향 참조 없음.

## 검증 결과

- `npx tsc --noEmit` → 통과 (오류 0)
- `npx eslint`(변경 파일 전체, react-hooks 포함) → CLEAN (`.eslintignore` deprecation 경고만, 기존부터 존재·무관)
- `npx vite build` → BUILD SUCCESSFUL

## QA 주의사항

- 목록 탭 `type` 파라미터는 **ID 기반**. 백엔드가 type 필터를 이름 문자열로 받도록 바뀌면 탭 onClick/active 비교를 이름 기반으로 되돌려야 함.
- 주문/발주 이력 섹션 표시 조건은 유형 라벨 문자열 휴리스틱에 의존. 실제 company_type 시드 라벨(예: 정확한 한글/영문 표기)에 "고객"/"공급" 등이 포함되는지 QA에서 확인 필요. (데이터가 있으면 라벨과 무관하게 표시되므로 누락 위험은 낮음.)
- 워크트리에 `node_modules`가 없어 검증을 위해 `npm install`을 1회 실행함. 빌드 산출물은 `src/main/resources/static`(gitignore 대상)로 출력되어 git 상태에 영향 없음.
