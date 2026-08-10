# 하네스 변경 이력

`CLAUDE.md`의 "하네스: Northwind 풀스택 개발" 섹션(스킬/에이전트 구조)이 왜 지금 형태로 진화했는지 기록하는 로그입니다. 하네스 구조를 변경할 때마다 이 파일 맨 아래에 새 행을 추가하세요. `SKILL.md`나 `CLAUDE.md` 본문에는 추가하지 않습니다.

| 날짜 | 변경 내용 | 대상 | 사유 |
|------|----------|------|------|
| 2026-06-03 | 초기 구성 | 전체 | - |
| 2026-06-07 | 프론트엔드 빌드 검증 단계 추가 (`npm run build`) | northwind-frontend, northwind-qa, SKILL.md | TS2367 타입 오류가 검증을 통과해 머지된 사고 재발 방지 |
| 2026-06-08 | "오케스트레이터 제약" 섹션 추가 — 구현/검증을 서브 에이전트에 위임하도록 명시 | SKILL.md | i18n 작업에서 오케스트레이터가 Phase 2/4를 건너뛰고 직접 구현·테스트까지 수행하고 `_workspace` 산출물 없이 완료 보고한 사례 재발 방지 |
| 2026-06-10 | Phase 0에서 작업 시작 시 `_workspace/task_{YYMMDD}_{HHMMSS}/` 폴더를 생성해 모든 산출물을 그 안에 저장하도록 변경 | SKILL.md | `_workspace`를 평면 구조로 쓰다 보니 추적이 어려워 사후에 수동으로 `task_*` 폴더로 재정리(a140c237)해야 했던 문제 재발 방지 |
| 2026-06-10 | 산출물 작성 서브 에이전트 생성 작업 순서 개선 | northwind-doc, SKILL.md | doc 폴더에 산출물 작성하지 않는 사례 재발 방지 |
| 2026-06-10 | 각 서브 에이전트 전용 스킬 4종 신설(`northwind-backend-scaffold`, `northwind-frontend-admin-crud`, `northwind-qa-boundary-check`, `northwind-doc-storyboard-sync`) — 레이어 템플릿/체크리스트/StoryBoard 갱신 패턴을 스킬로 분리. 각 에이전트 `.md`와 오케스트레이터 프롬프트의 중복 설명(체크리스트, tsc 빌드 검증 주의사항 등)을 스킬 참조로 축약 | northwind-backend-scaffold, northwind-frontend-admin-crud, northwind-qa-boundary-check, northwind-doc-storyboard-sync, northwind-backend, northwind-frontend, northwind-qa, northwind-doc, northwind-orchestrator | S-40/41/42(주문 관리) 등 다음 작업에서 반복될 i18n(`*ModelConst`/`*ErrorConst`)·DataGrid CRUD·경계면 비교·StoryBoard 동기화 작업의 일관성 확보, 동일 설명이 여러 파일에 중복되어 유지보수가 어려운 문제 해소 |
| 2026-06-12 | "사전 설계 분기 (ad-hoc)" 섹션 추가 — 기존 패턴이 없는 큰 구조 변경 요청은 `northwind-dev` 호출 전 `northwind-architect` 에이전트로 설계 합의를 거치도록 명시 | CLAUDE.md | 파이프라인에 상시 architecture agent를 추가하는 대신, Phase 1(계획 수립)을 오케스트레이터가 대화 맥락을 유지한 채 직접 수행하는 기존 구조를 깨지 않고 큰 구조 변경 시에만 ad-hoc으로 설계 검토를 끼워넣기 위함 |
| 2026-06-12 | `_workspace/task_*/` 산출물 생성 위치를 main의 `_workspace/`에서 worktree 내부 `{WORKTREE_PATH}/_workspace/{TASK_NAME}/`로 변경, Phase 1.5(worktree 생성)를 Phase 1에 통합 | SKILL.md | 산출물이 main에 남아 PR 머지 후 별도 커밋(`work: commit task doc of ...`)으로 수동 반영해야 했던 문제 해소 — feature 브랜치 커밋에 자연스럽게 포함되도록 함 |
| 2026-06-17 | 커밋 메시지 컨벤션을 `task(domain): description` 형식으로 명확화 — 특정 도메인 작업은 scope 표기, 프로젝트 전반 변경은 domain 생략 | CLAUDE.md | 여러 도메인을 관리하면서 커밋만으로 변경 대상 도메인을 식별하기 위함 |
| 2026-06-18 | N+1 쿼리·호출 방지 패턴 추가 — 백엔드(루프 내 개별 쿼리 금지), 프론트엔드(N번 개별 API 호출 금지), QA 체크리스트에 N+1 검증 항목 추가 | src/CLAUDE.md, frontend/CLAUDE.md, northwind-qa-boundary-check | 목록·배치 처리에서 루프 내 개별 쿼리/API 호출로 인한 성능 저하 및 DB 락 경합 방지 |
| 2026-07-14 | Phase 1 worktree 생성 직후 `frontend/node_modules`를 Windows junction으로 연결하는 단계 추가 | northwind-dev/SKILL.md | git worktree는 추적 파일만 복사하므로 worktree 단독으로 `npm run build`/`npm run dev` 실행이 불가능했던 문제 해소 |
| 2026-08-10 | CLAUDE.md에 인라인으로 있던 이 변경이력 테이블을 별도 파일(`CHANGELOG.md`)로 분리 | CLAUDE.md, northwind-dev/CHANGELOG.md | CLAUDE.md는 매 세션 자동 로드되는데, 이력 커밋의 75%가 실질 컨벤션 변경 없이 로그 행만 추가하는 커밋이라 `git log -- CLAUDE.md`로 실제 규칙 변경 이력을 추적하기 어려웠고, 다른 SKILL.md들에는 없는 CLAUDE.md만의 예외적 패턴이었음 |
