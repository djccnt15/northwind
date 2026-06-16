# 문서 반영 결과

QA 최종 판정: **PASS** (04_qa_report.md) → 문서 갱신 진행.

## 코드 기준 교차 검증

01_plan.md "doc/ 영향 범위"를 그대로 신뢰하지 않고 실제 컨트롤러/모델 코드를 직접 확인했다.
본 작업은 **백엔드 전용**(프론트엔드 변경 없음)이므로 `frontend/src/app/router.tsx` 라우트 검증은 해당 없음 — 화면(S-03/S-11) 자체는 이미 구현·등록된 ✅ 상태이고, 이번 작업은 그 화면이 쓰는 백엔드 API/정책을 구현한 것이다.

| 검증 항목 | 위치 | 결과 |
|-----------|------|------|
| `GET /api/v1/lang` (`isAuthenticated()`) | `domain/lang/controller/LangApiController.java:29-34` | 구현 확인 |
| `PATCH /api/v1/user/{userId}/lang` | `domain/user/controller/UserApiController.java:58-66` | 구현 확인 |
| 회원가입 `preferredLangCode` 필드 | `domain/user/model/SignupReq.java:70` | 구현 확인 |
| 회원가입 시 언어 할당 (`getLangOrDefault` → `updateLang`) | `domain/user/business/UserBusiness.java:47-48` | 구현 확인 |
| 언어 변경 즉시 세션 반영 | `domain/user/business/UserBusiness.java:97` (`userSession.setPreferredLang(...)`) | 구현 확인 |

## 변경된 문서

- **doc/StoryBoard.md**
  - **7절 "기존 화면 내 미구현 기능" 표**: S-11 "선호 언어 변경" 항목을 갱신.
    - 기존: 백엔드 API + 커스텀 `LocaleResolver`를 `northwind-architect` 검토 대상으로 표기(미구현).
    - 변경: 백엔드 API(`GET /api/v1/lang`, `PATCH /api/v1/user/{userId}/lang`) 및 커스텀 `UserLocaleResolver` **구현 완료**로 명시하고, 잔여 미구현 범위를 "프론트엔드 드롭다운/i18n 전환 연동"으로 한정.
    - (행 자체를 제거하지 않은 이유: 화면 내 프론트엔드 기능이 아직 미구현이므로 7절에 잔여 범위로 유지하는 것이 정확함)
  - **3절 S-11 "선호 언어 결정 정책 (로그인 전/후)"**: `SessionInfoRes.preferredLang` 추가와 커스텀 `LocaleResolver` 항목을 "설계 검토(northwind-architect) 대상" → "구현 완료(`WebConfig`의 `@Bean localeResolver()`로 등록, 비인증/미설정 시 `AcceptHeaderLocaleResolver` 폴백)"로 갱신.

## 변경 없음

- **1절 전체 화면 목록 표 / 3절 상세 헤더**: S-03(회원가입), S-11(내 프로필) 모두 **이미 ✅** 상태 — 화면 자체는 기존 구현 완료. 이번 작업은 화면이 사용하는 백엔드 API/정책 추가이므로 화면 단위 ✅/🔲 전환은 발생하지 않음.
- **6절 미구현 화면 개발 우선순위 표**: S-10 홈 대시보드만 남아 있으며 이번 작업과 무관 — 변경 없음.
- **doc/PRD.md**: i18n 전략 문서가 커밋 76e75736에서 이미 갱신됨. "명시적으로 만들지 않을 것" 항목 변경 없음 — 변경 없음.
- **doc/EDR.md**: `supported_lang` 테이블(L93), `app_user.preferred_lang_id` FK(L109), `supported_lang ||--o{ app_user`(L331) 관계가 **이미 ERD에 존재** — 신규 테이블/관계 추가 없음 → 변경 없음.

## 01_plan.md와 실제 코드 차이

- 01_plan.md는 "S-11 선호 언어 변경 항목이 ✅로 갱신될 수 있음"이라고 했으나, StoryBoard에서 S-11 "선호 언어 변경"은 화면 단위(S-XX) 상태가 아니라 **7절 화면 내 기능 항목**으로 관리되고 있었다. 또한 이번 작업은 백엔드 전용이라 프론트엔드 UI 연동이 빠져 있으므로, 7절 행을 통째로 제거하지 않고 "백엔드 완료 / 프론트엔드 잔여"로 정확히 분리해 기록했다.
