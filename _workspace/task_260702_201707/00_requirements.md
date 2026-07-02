# 요구사항

## 기능 설명

S-11 내 프로필 화면에 "선호 언어 변경" UI를 추가한다.
- `Language` 드롭다운: `GET /api/v1/lang`로 조회한 `supported_lang` 전체 목록(`id`, `lang`) 표시, 현재 선택값은 `userInfo.preferredLang`
- `[Change]` 버튼: `PATCH /api/v1/user/{userId}/lang` (`{ preferredLangId }`) 호출
- 성공 시: `UserInfoRes.preferredLang` 갱신 + 프론트엔드 i18n 언어(`i18n.changeLanguage`) 즉시 전환

## 범위: 프론트엔드 전용

백엔드 API(`GET /api/v1/lang`, `PATCH /api/v1/user/{userId}/lang`)와 인증 사용자용 `UserLocaleResolver`는 이미 main에 병합되어 있음 (`feature/backend-i18n`, PR #21, commit 6d461eb). 이번 작업은 프론트엔드 UI 및 관련 타입 정의만 추가한다.

## 참고 도메인 (유사한 기존 구현)

- `frontend/src/pages/profile.tsx` — 기존 프로필 화면. 비밀번호 변경(`onSubmitPassword`), 언어와 유사하게 별도 섹션 + `[Change]` 버튼 형태로 구현되어 있어 동일 패턴 재사용
- 백엔드 계약:
  - `LangApiController.getLangs()` → `GET /api/v1/lang` → `Api<List<LangRes>>`, `LangRes { id: Long, lang: String }`, 인증 필요(`@PreAuthorize("isAuthenticated()")`)
  - `UserApiController.updateLang()` → `PATCH /api/v1/user/{userId}/lang`, body `UpdateLangReq { preferredLangId: Long }` → `Api<UserInfoRes>`
  - `UserInfoRes.preferredLang: String` (언어 코드, 예: `"ko"`, `"en"`) — 이미 필드 존재
  - `SessionInfoRes.preferredLang: String` — check-session 응답에도 이미 존재 (로그인 직후 언어 전환에 사용 가능)

## 특이사항

- 프론트엔드 `entities/employee.ts`의 `UserIfs`, `entities/app/user.ts`의 `SessionIfs`에는 아직 `preferredLang` 필드가 없음 → 추가 필요
- 프론트엔드 어디에도 `i18n.changeLanguage()` 호출이 없음(`frontend/src/shared/i18n.ts`는 `i18next-browser-languagedetector`로 브라우저 언어만 감지) → 이번 작업에서 최초로 프로그래매틱 언어 전환을 도입
- i18n 설정(`shared/i18n.ts`)의 `supportedLngs: ["ko", "en"]`과 백엔드 `supported_lang` 테이블 값이 일치해야 함 (드롭다운은 백엔드 목록을 그대로 사용하므로 별도 매핑 불필요)
- 로그인 직후(check-session 성공 시) `preferredLang` 기준으로 i18n 언어를 맞출지 여부는 이번 작업 범위에 포함할지 QA/구현 단계에서 판단 필요 — PRD 요구사항은 "프로필 화면에서 변경 시 즉시 전환"만 명시하지만, 새로고침 후에도 유지되려면 로그인/세션 확인 시점의 동기화가 필요함

## Worktree
- 브랜치: feature/profile-preferred-lang
- 경로: C:/projects/northwind/.worktree/feature/profile-preferred-lang
