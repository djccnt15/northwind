# 구현 계획

## 백엔드 작업 항목

없음 (이미 main에 구현 완료: `GET /api/v1/lang`, `PATCH /api/v1/user/{userId}/lang`, `UserLocaleResolver`).

## 프론트엔드 작업 항목

### 1. entities (타입 정의)
- `frontend/src/entities/employee.ts`
  - `UserIfs`에 `preferredLang: string;` 필드 추가
  - `LangIfs { id: number; lang: string; }` 타입 신규 추가 (`GET /api/v1/lang` 응답 매핑용, `TitleIfs`/`TeamIfs`와 동일한 위치·스타일)
- `frontend/src/entities/app/user.ts`
  - `SessionIfs`에 `preferredLang: string;` 필드 추가 (check-session 응답에 이미 필드 존재, 로그인 직후 언어 동기화에 사용)
- `frontend/src/entities/index.ts`, `frontend/src/entities/app/index.ts`
  - 신규 `LangIfs` public export 추가 (기존 export 목록 패턴 따름)

### 2. features/auth (세션 동기화 시 i18n 전환)
- `frontend/src/features/auth/auth-context.tsx`
  - `responseToUser()`에서 `preferredLang: String(data.body?.preferredLang) || ""` 매핑 추가
- `frontend/src/app/provider/auth-provider.tsx` (파일 열어서 check-session 처리부 확인 후)
  - check-session 성공 시 `user.preferredLang`이 존재하면 `i18n.changeLanguage(preferredLang)` 호출하여 새로고침 후에도 서버 저장 언어가 유지되도록 함
  - `frontend/src/shared/i18n.ts`의 default export `i18n` 인스턴스를 import해서 사용

### 3. pages/profile.tsx (언어 변경 UI)
- 상단 import에 `i18n` (from `@/shared/i18n`), `LangIfs` 타입 추가
- 상태 추가: `langs: LangIfs[]`, `preferredLangId: number | ""`, `isLangLoading: boolean`
- `useEffect`로 마운트 시 `privateApi.get("/v1/lang")` 호출하여 `langs` 목록 채움 (기존 `fetchUserInfo` useEffect와 동일 패턴, 별도 useEffect로 분리하거나 병합 — 기존 코드 스타일에 맞춰 판단)
- `userInfo` 로드 후 현재 `preferredLang`(코드) → `langs` 목록에서 일치하는 `id`를 찾아 `preferredLangId` 초기값으로 설정
- UI: 기존 `liveUntil`/`team`/`title` 등이 있는 읽기 전용 정보 섹션 근처(또는 비밀번호 변경 폼과 유사한 위치)에 `Language` `<select>` 드롭다운(`langs` 옵션) + `[Change]` `SubmitBtn` 추가 (StoryBoard 목표 화면 참고: `Language [Korean ▼] [Change]`)
- `onSubmitLang` 핸들러:
  - `.then()`: 응답 `UserInfoRes`로 `userInfo`/`preferredLangId` 갱신 + `i18n.changeLanguage(data.body.preferredLang)` 호출 + 성공 alert(`t("page.profile.alerts.langUpdated")` 신규 키)
  - `.catch()`: 기존 패턴과 동일하게 `err.response?.data`의 `description` 또는 fallback 메시지(`t("page.profile.alerts.langUpdateFailed")` 신규 키) alert
  - `.finally()`: `isLangLoading` false
- API 호출: `privateApi.patch(`/v1/user/${user.id}/lang`, { preferredLangId })`

### 4. i18n 리소스 (신규 번역 키)
- `frontend/public/locales/ko/translation.json`, `frontend/public/locales/en/translation.json` (실제 경로는 `shared/i18n.ts`의 `loadPath: "/locales/{{lng}}/{{ns}}.json"` 기준으로 탐색 후 확정)
- 신규 키: `page.profile.fields.language`, `page.profile.placeholders.language`(필요 시), `page.profile.change`(이미 존재 — 비밀번호 섹션과 공용 가능 여부 확인 후 재사용 또는 신규 키), `page.profile.alerts.langUpdated`, `page.profile.alerts.langUpdateFailed`

## 참고 패턴

- `frontend/src/pages/profile.tsx`의 비밀번호 변경 섹션(`onSubmitPassword`, 별도 `<Form>` + `SubmitBtn`)을 그대로 따라 언어 변경 섹션을 별도 `<Form onSubmit={onSubmitLang}>`으로 구성
- 드롭다운 자체는 프로젝트에 기존 `<select>` 패턴이 없을 수 있음 → `frontend/CLAUDE.md` "폼 처리 패턴"에 따라 Controlled Component로 구현, 기존 `Input` styled-component와 유사한 새 `Select` styled-component 정의 또는 `Input`을 `as="select"`로 재사용 (기존 스타일 컨벤션 확인 후 판단)
- 팩토리 함수 패턴(`updateField`)은 이번 필드가 1개뿐이라 불필요, 개별 `onChangePreferredLang` 핸들러로 충분

## doc/ 영향 범위

- `doc/StoryBoard.md`:
  - S-11 내 프로필 섹션(라인 219~276 부근): ASCII 목표 화면의 `Language [Korean ▼] [Change]` 항목은 이미 ✅ 전제로 그려져 있었으나 실제 미구현 상태였음 → 구현 완료 후 관련 서술 갱신 필요 (현재 "**선호 언어 결정 정책**", "**선호 언어 변경 (프로필 화면)**" 소제목의 설명 텍스트가 계획 단계 서술이라면 완료형으로 수정)
  - §7 "기존 화면 내 미구현 기능" 표: `S-11 내 프로필 (프론트엔드) | 선호 언어 변경 UI ...` 행 — 이번 작업으로 해소되므로 표에서 제거 (다른 미구현 항목 2건은 유지)
- `doc/PRD.md`, `doc/EDR.md`: 영향 없음 (이미 i18n 전략 관련 내용이 반영되어 있고, 이번 작업은 StoryBoard에 이미 계획된 화면 요소의 구현에 해당)

## QA 중점 검토 항목

- `entities/app/user.ts`의 `SessionIfs.preferredLang`과 `entities/employee.ts`의 `UserIfs.preferredLang`이 백엔드 `SessionInfoRes.preferredLang` / `UserInfoRes.preferredLang` (둘 다 `String`)과 필드명·타입 일치하는지
- `PATCH /api/v1/user/{userId}/lang` 요청 body가 백엔드 `UpdateLangReq { preferredLangId: Long }`과 정확히 일치하는지 (필드명 `preferredLangId`, camelCase)
- i18n 언어 전환이 실제로 UI에 반영되는지 (react-i18next는 `i18n.changeLanguage()` 호출 시 리렌더링 트리거됨 — `useTranslation()` 훅 사용 컴포넌트는 자동 반영되는지 확인)
- `frontend/src/shared/i18n.ts`의 `supportedLngs: ["ko", "en"]`과 백엔드가 반환하는 `lang` 코드 값이 일치하는지 (불일치 시 `changeLanguage` 호출이 무시되거나 fallback으로 빠질 수 있음)
- 새로고침 후에도 언어가 유지되는지 (로그인/check-session 시점 동기화 로직이 실제로 동작하는지)
- 기존 `npm run build` 통과 여부 (신규 타입/JSON 키 오탈자 등)
