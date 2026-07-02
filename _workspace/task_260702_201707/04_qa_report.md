# QA 리포트

프론트엔드 전용 작업(선호 언어 변경 UI + 로그인/세션 시점 i18n 동기화). 백엔드는 이미 main에 병합된 기존 구현(`lang`, `user.updateLang`)을 재사용하므로 실제 백엔드 소스 코드와 경계면 교차 검증만 수행.

## 경계면 교차 비교 (백엔드 소스 ↔ 프론트엔드 타입)

| 경계면 | 백엔드 (실제 소스) | 프론트엔드 | 판정 |
|--------|-------------------|-----------|------|
| `GET /v1/lang` 응답 | `Api<List<LangRes>>`, `LangRes { Long id; String lang; }` (`LangApiController`, `LangRes`) | `ApiIfs<LangIfs[]>`, `LangIfs { id: number; lang: string; }` (`entities/employee.ts`) | 일치 |
| `PATCH /v1/user/{userId}/lang` body | `UpdateLangReq { Long preferredLangId }` (`UpdateLangReq`) | `{ preferredLangId }` (number) (`profile.tsx:355`) | 일치 (필드명 `preferredLangId` camelCase 동일) |
| `PATCH .../lang` 응답 | `Api<UserInfoRes>`, `preferredLang: String` (`UserInfoRes:33`) | `ApiIfs<UserIfs>`, `UserIfs.preferredLang: string` (`entities/employee.ts:29`) | 일치 |
| `SessionInfoRes.preferredLang` | `String` (`SessionInfoRes:18`) | `SessionIfs.preferredLang: string` (`entities/app/user.ts:5`) | 일치 |

- 인증 필요 엔드포인트(`/api/v1/*`)는 모두 `privateApi`로 호출(`profile.tsx`의 `/v1/lang`, `/v1/user/${id}/lang`), 로그인만 공개 `api` 사용 — 올바름.
- 응답 래퍼 `ApiIfs<T>` 사용 일관됨.
- `LangApiController`는 `@PreAuthorize("isAuthenticated()")`로 보호 → 프론트 `privateApi` 호출과 정합.

## QA 중점 검토 항목 결과 (01_plan.md 기준)

1. **필드명·타입 일치**: `SessionIfs.preferredLang` / `UserIfs.preferredLang` 모두 `string` ↔ 백엔드 `String` 일치. PASS
2. **PATCH body 필드명**: `{ preferredLangId }` ↔ `UpdateLangReq.preferredLangId` 일치. PASS
3. **i18n.changeLanguage 실제 반영**: `useTranslation()` 훅을 사용하는 컴포넌트는 `changeLanguage` 호출 시 react-i18next가 리렌더 트리거. profile/login 모두 훅 사용. PASS
4. **supportedLngs ↔ 백엔드 lang 코드**: seed `supported_lang` = `('en'), ('ko')` (`data-h2.sql:1-2`) ↔ `shared/i18n.ts` `supportedLngs: ["ko", "en"]` 코드값 일치. fallback으로 빠지지 않음. PASS
5. **새로고침 후 언어 유지**: `auth-provider.tsx`의 check-session(code 1200) 성공 시 `sessionUser.preferredLang && i18n.language !== preferredLang` 조건에서 `changeLanguage` 호출 → 새로고침 후 서버 저장 언어 복원. PASS
6. **npm run build**: 통과. PASS

## Critical 수정 사항
없음.

## Major 수정 사항
없음. FSD 레이어 의존 방향(app→pages→features→entities→shared) 준수, `entities/` 외부 `~Ifs` 정의 없음, `import type` 사용, API 호출 `.then().catch().finally()`(또는 조회는 `.then().catch()`) 체이닝 일관, N+1 호출 없음(`/v1/lang` 단건 조회).

## Minor 수정 사항
없음(수정 요함). 참고 사항만 기록:
- `auth-provider.tsx:17`의 `console.log("Session check response:", data)`, `login.tsx:96`의 `console.log("login response:", data)`는 이번 작업 이전부터 존재하던 디버그 로그로 이번 변경 범위 밖. 별도 정리 작업에서 다룰 것을 권고(이번 판정에는 미반영).
- `React.SubmitEvent<HTMLFormElement>` 타입은 기존 `onSubmitProfile`/`onSubmitPassword`와 동일 패턴을 신규 `onSubmitLang`이 따른 것으로 일관성 유지. 빌드 통과로 타입 유효성 확인됨.

## 테스트 결과
- 백엔드: SKIP (백엔드 변경 없음 — 기존 main 병합 구현 재사용). 계약은 실제 소스 코드(`LangApiController`, `LangRes`, `UpdateLangReq`, `UserInfoRes`, `SessionInfoRes`, `UserApiController.updateLang`)로 교차 검증 완료.
- 프론트엔드: PASS (`cd frontend; npm run build` → `tsc -b && vite build` 성공, `✓ built in 2.46s`). 경고는 기존과 동일한 청크 크기(>500kB) 및 MUI 내부 `eval` 경고뿐이며 타입 오류 없음.

## 최종 판정
PASS
