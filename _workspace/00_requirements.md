# 요구사항

## 기능 설명
백엔드 DTO 검증(validation) 및 에러 응답 메시지에 i18n(국제화)을 적용한다.

1. **i18n 적용**: `messages`/`errors` 메시지 번들(영어/한국어)을 `MessageSource`에 연동하고, 검증 메시지·예외 응답 메시지를 `MessageUtil`을 통해 현재 로케일에 맞게 응답하도록 변경한다.
2. **에러 메시지 키 상수화**: `messageUtil.getMessage("error.user.passwordMismatch")`처럼 호출부에 직접 하드코딩된 메시지 키 문자열을 모두 `*ErrorConst` 유틸리티 클래스의 `final String` 상수로 추출한다.

## 범위: 백엔드 전용

## 참고 도메인 (유사한 기존 구현)
- `domain/team/validation/TeamModelConst.java` — `*ModelConst` 상수 클래스 패턴 (`@UtilityClass`, `static import`)
- `global/exception/exceptions/ApiException.java`, `global/exception/advice/*` — 예외 처리 핸들러 구조

## 특이사항
- i18n용 베이스 리소스 파일(`messages*.properties`, `errors*.properties`)은 commit `d57696e`("feat: create base message files for `i18n`")에서 이미 생성되어 있음 — 이번 작업은 해당 리소스를 실제로 연동하는 단계
- `MessageSource` + `LocaleContextHolder.getLocale()`을 감싼 `MessageUtil` 컴포넌트를 신규 작성해야 함
- 기존 `*ModelConst`(Bean Validation 어노테이션 `message` 속성용 `{key}` placeholder)와 신규 `*ErrorConst`(메시지 키 평문 문자열, `messageUtil.getMessage()` 인자)는 역할이 다르므로 별도 클래스로 분리

## Worktree
- 브랜치: feature/backend-i18n-validation-error
- 경로: C:/projects/northwind/.worktree/feature/backend-i18n-validation-error
</content>
