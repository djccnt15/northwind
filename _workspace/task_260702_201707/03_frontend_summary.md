# 프론트엔드 구현 요약

S-11 내 프로필 화면에 "선호 언어 변경" UI를 추가하고, 로그인/세션 확인 시점에 서버 저장 언어를 i18n에 동기화하도록 구현했다.

## 생성/수정된 파일

### entities (타입 정의)
- `frontend/src/entities/employee.ts`
  - `LangIfs { id: number; lang: string; }` 신규 추가 (`GET /v1/lang` 응답 매핑용)
  - `UserIfs`에 `preferredLang: string;` 필드 추가
- `frontend/src/entities/app/user.ts`
  - `SessionIfs`에 `preferredLang: string;` 필드 추가
- `frontend/src/entities/index.ts`
  - `LangIfs` public export 추가

### features/auth
- `frontend/src/features/auth/auth-context.tsx`
  - `responseToUser()`에 `preferredLang: String(data.body?.preferredLang) || ""` 매핑 추가

### app/provider
- `frontend/src/app/provider/auth-provider.tsx`
  - `shared/i18n`의 `i18n` 인스턴스 import
  - check-session 성공(code 1200) 시 `sessionUser.preferredLang`이 존재하고 현재 i18n 언어와 다르면 `i18n.changeLanguage(preferredLang)` 호출 → 새로고침 후에도 서버 저장 언어 유지

### pages
- `frontend/src/pages/profile.tsx`
  - `i18n`(`@/shared/i18n`), `LangIfs` 타입 import
  - 상태: `langs: LangIfs[]`, `preferredLangId: number | ""`, `isLangLoading: boolean`
  - 마운트 시 `privateApi.get("/v1/lang")`로 언어 목록 조회 (별도 useEffect)
  - `userInfo.preferredLang`(코드) ↔ `langs` 매칭으로 `preferredLangId` 초기값 설정 (derived useEffect)
  - `Select` styled-component + `[Change]` 버튼으로 언어 변경 `<Form onSubmit={onSubmitLang}>` 추가 (비밀번호 폼과 동일 위치·패턴)
  - `onSubmitLang`: `privateApi.patch(`/v1/user/${user.id}/lang`, { preferredLangId })` → 성공 시 `userInfo` 갱신 + `i18n.changeLanguage(data.body.preferredLang)` + 성공 alert, `.catch()`/`.finally()` 기존 패턴 준수
- `frontend/src/pages/login.tsx`
  - 로그인 성공 직후 `sessionUser.preferredLang` 기준 `i18n.changeLanguage()` 호출 추가 (로그인 즉시 언어 반영, 새로고침 대기 불필요)

### i18n 리소스 (신규 키)
- `frontend/public/locales/ko/translation.json`, `frontend/public/locales/en/translation.json`
  - `page.profile.fields.language`
  - `page.profile.placeholders.language`
  - `page.profile.languages.ko` / `page.profile.languages.en` (드롭다운 표시명, 백엔드 `lang` 코드 키로 조회하며 미정의 코드는 코드 원문 fallback)
  - `page.profile.alerts.langEmpty` / `langUpdated` / `langUpdateFailed`
  - `[Change]` 버튼은 기존 `page.profile.change` 키 재사용

## 추가된 라우트
없음 (기존 `/profile` 페이지 내 기능 추가, 라우터 변경 없음).

## 백엔드 계약 대조 (경계면 일치 확인)
| 항목 | 백엔드 | 프론트엔드 | 일치 |
|------|--------|-----------|------|
| `GET /v1/lang` 응답 | `List<LangRes>` `{ id: Long, lang: String }` | `ApiIfs<LangIfs[]>` `{ id: number, lang: string }` | O |
| `PATCH /v1/user/{userId}/lang` body | `UpdateLangReq { preferredLangId: Long }` | `{ preferredLangId }` (number) | O |
| `PATCH .../lang` 응답 | `Api<UserInfoRes>` (`preferredLang: String`) | `ApiIfs<UserIfs>` (`preferredLang: string`) | O |
| `SessionInfoRes.preferredLang` | `String` | `SessionIfs.preferredLang: string` | O |

## 주요 구현 사항 / QA 주의사항
- **supportedLngs 일치 확인 완료**: 백엔드 seed(`src/test/resources/data-h2.sql`)의 `supported_lang` 값은 `('en'), ('ko')`이며, `frontend/src/shared/i18n.ts`의 `supportedLngs: ["ko", "en"]`과 코드값이 정확히 일치한다. → `changeLanguage` 호출이 fallback으로 빠지지 않음.
- **언어 지속성**: 프로필 변경 시 즉시 전환 + check-session/로그인 시점 동기화로 새로고침 이후에도 서버 저장 언어가 유지된다.
- **드롭다운 표시명**: 백엔드는 `lang` 코드(`ko`/`en`)만 반환하므로, 표시명은 i18n 키 `page.profile.languages.<code>`로 렌더하고 미정의 코드는 코드 원문을 fallback으로 표시한다. 백엔드에 언어가 추가될 경우 번역 키만 추가하면 되며, 키가 없어도 코드로는 노출된다.
- **React Compiler 활성화**로 `useMemo`/`useCallback` 수동 작성 없음. FSD 레이어 의존 방향(`app → pages → features → entities → shared`) 준수, 역참조 없음.
- **의존성 설치**: 이 worktree의 `frontend/node_modules`가 없어 `npm ci`로 설치 후 빌드했다.

## 빌드 검증
`cd frontend; npm run build` (`tsc -b && vite build`) 통과. 출력 경고는 기존과 동일한 청크 크기/MUI 내부 eval 경고뿐이며 타입 오류 없음.
