---
name: northwind-qa-boundary-check
description: >
  Northwind 백엔드/프론트엔드 구현물의 "경계면 교차 비교" QA를 수행하기 위한 절차와 통합 체크리스트.
  02_backend_contract.md의 API 응답 필드와 frontend/src/entities의 ~Ifs 인터페이스를 필드 단위로 대조하고,
  레이어 책임/i18n/FSD 컨벤션 위반을 점검한다. "QA 검토", "컨벤션 검증", "경계면 점검",
  "백엔드-프론트엔드 타입 일치 확인" 등의 작업에서 사용한다.
---

## 경계면 교차 비교 절차 (핵심)

존재 확인만으로는 충분하지 않다. 다음 순서로 양쪽을 동시에 열어 필드 단위로 대조한다:

1. `02_backend_contract.md`의 "응답 타입 정의" 섹션에서 각 엔드포인트의 응답 필드명·타입을 추출한다.
2. `frontend/src/entities/`에서 대응하는 `~Ifs` 인터페이스를 찾는다 (`03_frontend_summary.md`의 "생성/수정된 파일" 참고).
3. 필드명·타입을 1:1로 대조한다 — 누락된 필드, 타입 불일치(`number` vs `string`), nullable 여부 차이를 모두 기록한다.
4. 인증 필요 엔드포인트(`/api/v1/*`)가 프론트엔드에서 `privateApi`로 호출되는지 확인한다.
5. 응답 래퍼(`ApiIfs<T>`/`PageIfs<T>`) 사용이 일관적인지 확인한다.

## 백엔드 체크리스트 (`src/CLAUDE.md`)

- [ ] 레이어 책임: Controller(요청/응답)/Service(단일 Repo)/Business(복수 Service 조합 + `@Transactional`)/Converter(변환만) 혼재 없음
- [ ] Business 레이어 기준: 단순 도메인에 불필요한 Business 없음 / 복잡 도메인에 Business 누락 없음
- [ ] 모든 엔드포인트가 `ResponseEntity<Api<T>>` 반환
- [ ] 예외는 `ApiException` + `StatusCode`만 사용
- [ ] i18n: `*ModelConst`/`*ErrorConst` 분리, 테스트 프로파일 `messages.basename` 동일 설정, `@WebMvcTest`에 `@MockitoBean MessageUtil`
- [ ] N+1 쿼리 방지: Business/Service 레이어에서 루프 내 개별 쿼리 호출 없음 (1회 조회 + 메모리 매핑)
- [ ] N+1 lazy loading 방지: 목록 조회 Converter가 접근하는 관계 필드가 Repository 메서드에서 `@EntityGraph` 또는 `JOIN FETCH`로 pre-fetch되는지 교차 확인
- [ ] 신규 기능에 테스트 존재 + 통과

## 프론트엔드 체크리스트 (`frontend/CLAUDE.md`)

- [ ] FSD 레이어 역방향 참조 없음 (`app→pages→widgets→features→entities→shared`)
- [ ] `entities/` 외부에 `~Ifs` 인터페이스 정의 없음
- [ ] 미사용 변수/파라미터 없음, `as` 강제 캐스팅 없음, `import type` 사용
- [ ] API 호출이 `.then().catch().finally()` 체이닝
- [ ] N+1 API 호출 방지: useEffect/이벤트 핸들러에서 forEach/map으로 동일 API를 N번 호출하는 패턴 없음 (배치 API 1회 호출)

## 검증 실행

- 백엔드: `.\gradlew.bat test`
- 프론트엔드: `cd frontend; npm run build` (`npx tsc --noEmit` 단독 실행 금지 — 사유는 `northwind-frontend-admin-crud` 스킬의 "빌드 검증" 참고)

## 중요도 분류 및 보고

Critical(빌드/런타임 오류 가능) → Major(컨벤션 위반) → Minor(스타일) 순으로 분류한다. 발견 즉시 수정(fix-first)하고, `04_qa_report.md`에 위반 내용·수정 내용·이유를 기록한 뒤 PASS/CONDITIONAL PASS/FAIL로 최종 판정한다.
