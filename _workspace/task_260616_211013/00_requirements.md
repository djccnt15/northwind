# 요구사항

## 기능 설명

커밋 76e75736("doc: add content about i18n strategy")에서 확정된 다국어 지원 전략을 백엔드에 구현한다.

- **비인증 요청**: Spring 기본 `AcceptHeaderLocaleResolver` (Accept-Language 헤더 기반)
- **인증 요청**: 사용자의 `preferred_lang_id` → 커스텀 `LocaleResolver`가 `UserSession.preferredLang`을 읽어 Locale 설정
- 로그인/check-session 응답에 `preferredLang` 필드 추가
- 회원가입 시 브라우저 언어(`preferredLangCode`) 전달 → DB에 `preferred_lang_id` 저장 (없으면 'en' 폴백)
- `PATCH /api/v1/user/{userId}/lang` API 신규 추가
- `GET /api/v1/lang` API 신규 추가 (지원 언어 목록 조회)

## 범위: 백엔드 전용

프론트엔드 Phase는 생략. 백엔드 API 구현만 수행.

## 이미 존재하는 것 (구현 불필요)

- `SupportedLangEntity` (id, lang), `AppUserEntity.preferredLang` (ManyToOne LAZY)
- `SupportedLangRepo` (JpaRepository 상속, 추가 메서드 없음)
- `messages*.properties` / `errors*.properties` (EN/KO 번역 완료)
- `MessageUtil` (@Component), `*ErrorConst` / `*ModelConst` 패턴
- `LangCreateTest` (sampledata/) — dev DB에 'en', 'ko' 삽입 코드 완성

## 참고 도메인 (유사한 기존 구현)

- Lang 도메인 → `domain/team/` (단순 조회, Business 없음, Controller → Service 직접 호출)
- User 도메인 수정 → `domain/user/` 기존 패턴 확장 (Business 있음)

## 특이사항

- `UserSession`은 Spring Security `UserDetails` 구현체 (`@Data @Builder`) — `preferredLang: String` 필드 추가 후 `AuthService.loadUserByUsername()`에서 설정
- `AppUserRepo.findWithRoleFirstByUsername` EntityGraph에 `"preferredLang"` 추가 필요 (LAZY N+1 방지)
- `AppUserRepo.findFullFirstById` EntityGraph에도 `"preferredLang"` 추가 필요
- LocaleResolver 빈 이름은 Spring MVC가 `localeResolver`를 자동 탐지함 → `WebConfig`에서 `@Bean` 등록
- H2 테스트 DB에 `supported_lang` 시드 데이터 없음 → `data-h2.sql` 추가 필요
- 언어 변경 후 현재 세션 즉시 반영: `userSession.setPreferredLang(langEntity.getLang())`

## Worktree

- 브랜치: feature/backend-i18n
- 경로: C:/projects/northwind/.worktree/feature/backend-i18n
