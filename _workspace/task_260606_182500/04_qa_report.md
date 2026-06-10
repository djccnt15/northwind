# QA 리포트

대상 브랜치: `feature/frontend-permission-routes`
검토 범위: 프론트엔드 전용 (백엔드 권한 체계 변경 commit 73b9be66 반영)
검토 파일:
- `frontend/src/app/provider/redirect-route.tsx`
- `frontend/src/app/router.tsx`
- `frontend/src/widgets/navbar-left.tsx`

---

## Critical 수정 사항

없음. (빌드/런타임 오류 가능성 없음 — `tsc -b` EXIT=0, 백엔드 테스트 BUILD SUCCESSFUL)

---

## Major 수정 사항

없음. 컨벤션 위반 미발견. 상세 검증 결과는 아래 참고.

### 검증 결과 (이상 없음)

1. **가드 패턴 일관성 (`redirect-route.tsx`)** — PASS
   - `ProductRoute`, `ManagerRoute` 모두 기존 `AdminRoute`와 동일한 `!user || !(...)` 형태로 작성.
   - 비로그인 사용자(`!user`)를 단락 평가로 먼저 차단하여 `user.authorities` 접근 시 런타임 오류 없음.
   - `ProductRoute`는 `ADMIN || PRODUCT`, `ManagerRoute`는 `ADMIN || MANAGER` 로 StoryBoard(`00_requirements.md`) 권한 기준과 일치.

2. **Layout 래핑 (`router.tsx`)** — PASS
   - `ProductRoute > Layout`, `ManagerRoute > Layout` 모두 `AdminRoute > Layout`, `ProtectedRoute > Layout` 패턴과 동일하게 가드가 `<Layout />`을 감싸는 구조.
   - `/products*` 3개 라우트는 별도 `ProductRoute` 그룹(`path="/"`)으로 분리, `/admin/categories`는 별도 `ManagerRoute` 그룹(`path="/admin"`)으로 분리. 나머지 admin 라우트는 `AdminRoute` 유지.
   - import 정렬(알파벳순) 및 상대 경로(`./provider/...`) 사용 — 기존 파일 컨벤션과 일치.

3. **내비게이션 조건부 렌더링 (`navbar-left.tsx`)** — PASS
   - Products 링크: `ADMIN || PRODUCT`, Admin-Category 링크: `ADMIN || MANAGER` 로 가드/StoryBoard와 일치.
   - Admin-User/Title/Team, OpenAPI(Swagger): `ADMIN` 전용 유지.
   - 권한 비교에 `user?.authorities.includes(...)` 옵셔널 체이닝 사용 — 기존 navbar 컨벤션과 일치.
   - Category 링크를 별도 `NavList` 블록으로 분리 — ADMIN 전용 블록과 표시 조건이 다르므로 분리가 타당.

4. **타입 정의 위치 / FSD 레이어** — PASS
   - 신규 인라인 `~Ifs` 정의 없음. 기존 `ChildNodeIfs`(`entities/app`) 재사용.
   - 의존 방향 준수: `app`(router, redirect-route) → `widgets`(navbar-left) → `features`(useAuth) → `entities`. 역방향 참조 없음.

5. **TypeScript strict 모드** — PASS
   - `npx tsc -b` EXIT=0 (worktree, node_modules 설치 상태에서 검증).
   - `noUnusedLocals` / `noUnusedParameters` 위반 없음 (`router.tsx`의 신규 import `ManagerRoute`, `ProductRoute` 모두 사용됨).
   - `verbatimModuleSyntax`: `import type { ChildNodeIfs }` 로 타입 전용 import 준수.

---

## Minor 수정 사항

없음.

---

## 경계면 검증 (백엔드 ↔ 프론트엔드)

| 화면 | 경로 | StoryBoard 기준 | 프론트(가드/navbar) | 백엔드 `@PreAuthorize` | 판정 |
|------|------|----------------|---------------------|------------------------|------|
| 상품 목록/상세 | `/products*` | ADMIN, PRODUCT | `ADMIN \|\| PRODUCT` | `hasAnyAuthority('PRODUCT')` | 프론트=StoryBoard 일치 / 백엔드 불일치(아래 참고) |
| 카테고리 관리 | `/admin/categories` | ADMIN, MANAGER | `ADMIN \|\| MANAGER` | `hasAnyAuthority('ADMIN', 'MANAGER')` | 완전 일치 |

### 참고 (범위 외, 백엔드 측 잠재 불일치 — 본 작업에서 수정하지 않음)

- `ProductApiController`는 `@PreAuthorize("hasAnyAuthority('PRODUCT')")` 로 설정되어 있어 **ADMIN 권한만 보유하고 PRODUCT가 없는 사용자는 `/api/v1/products` 접근 시 403** 을 받는다. (`RoleHierarchy` 빈 미설정 — `hasAnyAuthority`는 정확 일치)
- 반면 StoryBoard와 프론트엔드는 `ADMIN, PRODUCT`(ADMIN OR PRODUCT)를 허용한다. 즉 ADMIN-only 사용자는 프론트에서 Products 메뉴/라우트를 통과하지만 API에서 차단될 수 있다.
- 본 작업은 **프론트엔드 전용 범위**이며, 프론트엔드는 권위 기준인 StoryBoard(`00_requirements.md`)를 정확히 구현했으므로 프론트 코드는 수정하지 않는다. 백엔드 `ProductApiController`의 권한을 `hasAnyAuthority('ADMIN', 'PRODUCT')` 로 확장할지는 백엔드 담당자가 StoryBoard 기준으로 판단할 사항으로 별도 제기한다.
- 인증 경로 일치: 모든 보호 라우트는 세션 인증(`/api/v1/*`) 기반이며 프론트는 가드 + `privateApi` 흐름을 따른다. 불일치 없음.

---

## 테스트 결과

- **백엔드**: PASS — `.\gradlew.bat test` BUILD SUCCESSFUL (exit 0), 메인 워킹 트리에서 실행. (프론트엔드 전용 변경이므로 백엔드 회귀 없음 확인)
- **프론트엔드 타입체크**: PASS — `npx tsc -b` EXIT=0 (worktree).
- 프론트엔드 단위 테스트: 해당 변경(라우트 가드/navbar)에 대한 RTL 테스트는 미작성. 라우팅 가드는 통합 시나리오성 검증 대상으로, 현 변경 규모상 수동 시나리오 검증(요구사항 문서 권장 시나리오)으로 대체 가능. (개선 권장 항목으로 기록)

---

## 최종 판정

**PASS**

- 3개 검토 파일 모두 기존 컨벤션(`frontend/CLAUDE.md`) 및 StoryBoard 권한 기준과 일치.
- 빌드/타입체크/백엔드 테스트 모두 통과.
- Critical/Major/Minor 수정 사항 없음 (코드 수정 불필요).
- 단, 백엔드 `ProductApiController` 권한이 StoryBoard와 어긋날 수 있는 점은 범위 외 이슈로 별도 제기 (프론트 코드 책임 아님).
