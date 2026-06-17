# 요구사항

## 기능 설명

Northwind 프론트엔드에 react-i18next를 사용한 다국어(ko/en) 지원을 추가한다.
현재 UI 텍스트가 영어/한국어 혼재 상태이며, 전체 페이지의 하드코딩 문자열을 번역 키로 교체한다.

- 지원 언어: ko, en (fallback: en)
- 초기 언어: 브라우저 언어 자동 감지 (i18next-browser-languagedetector)
- 백엔드 에러 메시지: Spring MessageSource가 Accept-Language에 맞게 내려줌 → 프론트 번역 불필요 (result.description은 그대로 표시)

## 범위: 프론트엔드 전용

백엔드 i18n(Spring MessageSource)은 이미 구현 완료. 프론트엔드만 작업.

## 참고 도메인 (유사한 기존 구현)

- 없음 (신규 인프라 도입)
- react-i18next@17.0.8 이미 설치됨, i18next/i18next-browser-languagedetector/i18next-http-backend 추가 설치 필요

## 특이사항

- `verbatimModuleSyntax` 활성화 → 타입 import 시 `import type` 필수
- `noUnusedLocals`, `noUnusedParameters` 활성화 → 미사용 변수 컴파일 오류
- 빌드 검증은 반드시 `npm run build` 사용 (`npx tsc --noEmit` 단독 실행 금지)
- `vite.config.ts`의 `build.outDir`이 `../src/main/resources/static` → `public/locales/`는 빌드 시 자동 복사됨
- stock-take.tsx는 한국어 텍스트만 있는 유일한 페이지 → ko/en 번역 모두 필요

## Worktree
- 브랜치: feature/frontend-i18n
- 경로: C:/projects/northwind/.worktree/feature/frontend-i18n
