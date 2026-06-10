---
name: northwind-frontend-admin-crud
description: >
  Northwind 프론트엔드(React 19 + FSD)에서 MUI DataGrid 기반 관리자 CRUD 페이지를 추가할 때 사용하는
  타입 정의/페이지/라우터 템플릿과 체크리스트. "관리자 페이지 추가", "목록/상세 화면 구현",
  "DataGrid CRUD", "엔티티 타입 정의 및 라우터 등록" 등 신규 프론트엔드 페이지 구현 작업에서 사용한다.
---

## 핵심 체크리스트 (FSD 레이어 순서)

신규 화면 `<Domain>` 추가 시 다음 순서로 작업한다 (`frontend/CLAUDE.md` 기준):

1. `entities/<domain>.ts` — `<Domain>Ifs` 인터페이스 정의 (컴포넌트 파일에 인라인 금지, 타입은 `entities/`에만)
2. (필요 시) `features/<domain>/` — 재사용 로직/훅
3. `pages/admin-<domain>.tsx` (또는 `<domain>.tsx`) — DataGrid CRUD 페이지
4. `app/router.tsx` — 라우트 등록 + 적절한 가드(`AdminRoute`/`ManagerRoute`/`ProductRoute`/`ProtectedRoute`) 선택

레이어 의존 방향: `app → pages → widgets → features → entities → shared` (역방향 참조 금지)

DataGrid CRUD 페이지 템플릿: `references/admin-crud-page-template.md` 참고.

## API 호출 체크리스트

- [ ] 인증 필요 API는 `privateApi`, 공개 API는 `api` (`shared/api.ts`)
- [ ] `.then().catch().finally()` 체이닝 사용 (`async/await` 미사용)
- [ ] 응답 타입은 `ApiIfs<T>`/`PageIfs<T>`로 캐스팅 (`entities/app/api.ts`)
- [ ] 검증 오류(`result.code === 1400`)는 `body`가 `{ field: message }` Map

## 빌드 검증

구현 완료 후 `cd frontend; npm run build`로 검증한다. **`npx tsc --noEmit` 단독 실행은 금지** — 루트 `tsconfig.json`은 `"files": []`인 솔루션 스타일 설정이라 0개 파일을 검사하고 거짓으로 "오류 없음"을 출력한다. `npm run build`(`tsc -b && vite build`)만이 실제 프로젝트 설정(`tsconfig.app.json`, `noUnusedLocals`/`noUnusedParameters`/`verbatimModuleSyntax` 등)을 사용해 검증한다.

## 라우트 가드 선택

| 가드 | 조건 |
|------|------|
| `ProtectedRoute` | 로그인 필요 |
| `AdminRoute` | `ADMIN` 권한 필요 |
| `ManagerRoute` | `ADMIN` 또는 `MANAGER` |
| `ProductRoute` | `ADMIN` 또는 `PRODUCT` |
