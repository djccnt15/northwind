# 문서 반영 결과

## QA 판정 확인
- `04_qa_report.md` 최종 판정: **PASS** → 문서 갱신 진행.

## 코드 기준 검증 (01_plan.md "doc/ 영향 범위" 교차 검증)
프론트엔드 전용 작업이므로 라우터가 아닌 실제 페이지 코드로 검증함(신규 라우트 없음 — 기존 `/profile` 내 기능 추가).

- `frontend/src/pages/profile.tsx`
  - 언어 `Select` 드롭다운 + `[Change]` `SubmitBtn` UI 존재 확인 (474~495행, `<Form onSubmit={onSubmitLang}>`)
  - `GET /v1/lang` 조회 존재 확인 (`privateApi.get("/v1/lang")`, 245행)
  - `PATCH /v1/user/${user.id}/lang` 호출 존재 확인 (body `{ preferredLangId }`, 355행)
  - 성공 시 `i18n.changeLanguage(updatedLang)` 즉시 전환 확인 (361행)
  - `userInfo.preferredLang` ↔ `langs` 매칭으로 드롭다운 초기값 설정 확인 (259~264행)
- 계약 일치는 `04_qa_report.md` 경계면 교차 비교(백엔드 소스 ↔ 프론트 타입)에서 이미 PASS로 확인됨.

## 변경된 문서
- `doc/StoryBoard.md`
  - §3 S-11 내 프로필: "선호 언어 변경 (프로필 화면)" 소제목을 "(구현 완료)"로 표기하고, `GET /api/v1/lang` / `PATCH .../lang` / i18n 즉시 전환 / `SessionInfoRes`·`UserInfoRes` `preferredLang` 필드 및 로그인·check-session 동기화 서술을 계획형에서 완료형으로 갱신 (기존 "(신규)", "필드 추가 필요" 등 계획 단계 표현 제거).
  - §7 "기존 화면 내 미구현 기능" 표: `S-11 내 프로필 (프론트엔드) | 선호 언어 변경 UI ...` 행 제거. 나머지 2개 행(S-02 소셜 로그인 버튼, S-11 소셜 계정 연동 관리)은 유지.
  - §1 화면 목록 S-11 상태: 이미 ✅이므로 변경 없음.
  - §6 미구현 화면 개발 우선순위 표: 선호 언어 관련 항목 없음(S-10만 존재) → 변경 없음.

## 변경 없음
- `doc/PRD.md`: "개인화 언어 설정" / "인증 상태별 언어 결정 전략" 항목은 화면별 구현 상태가 아닌 제품 정책·요구사항 서술이며 이번 작업으로 정책이 바뀌지 않음. 갱신 대상 아님(01_plan.md "영향 없음"과 실제 코드 상태 일치).
- `doc/EDR.md`: `supported_lang` 테이블과 `app_user.preferred_lang_id` FK, `supported_lang ||--o{ app_user` 관계가 이미 ERD에 반영되어 있고 이번 작업은 스키마 변경 없음. 갱신 대상 아님.
