---
name: northwind-dev
description: >
  Northwind 풀스택 기능 개발을 에이전트 팀으로 조율하는 오케스트레이터.
  백엔드 도메인 구현(entity/repo/service/controller/test), 프론트엔드 FSD 구현(entities/features/pages/router), QA 검증을 순차 수행.
  "기능 추가", "도메인 구현", "API 개발", "페이지 구현", "풀스택 개발", "백엔드 추가", "프론트 구현",
  "재구현", "다시 만들어", "수정", "보완", "업데이트", "개선" 등 Northwind 개발 요청이 오면 반드시 이 스킬을 사용하라.
  단순 코드 설명이나 개념 질문은 직접 응답해도 된다.
---

## 실행 모드

**서브 에이전트 파이프라인** — 각 Phase가 독립적 산출물을 파일로 전달하며, 순차적으로 실행된다.

```
오케스트레이터
 ├── Phase 0: 컨텍스트 확인
 ├── Phase 1: 요구사항 분석
 ├── Phase 2: 백엔드 구현 (northwind-backend sub-agent)
 ├── Phase 3: 프론트엔드 구현 (northwind-frontend sub-agent) — 풀스택일 때만
 ├── Phase 4: QA 검토 (northwind-qa sub-agent)
 └── Phase 5: 결과 보고
```

---

## Phase 0: 컨텍스트 확인

`_workspace/` 디렉토리 존재 여부를 확인한다.

- **`_workspace/` 없음** → 초기 실행: Phase 1부터 진행
- **`_workspace/` 있음 + 사용자가 새 기능 요청** → 새 실행: 기존 `_workspace/`를 `_workspace_prev/`로 이동 후 진행 + 새 worktree 생성
- **`_workspace/` 있음 + 사용자가 "수정/보완" 요청** → 부분 재실행: `_workspace/00_requirements.md`에서 `WORKTREE_PATH`를 읽어 기존 worktree 재사용, 해당 Phase 에이전트만 재호출

---

## Phase 1: 요구사항 분석 + Worktree 생성

사용자 요청을 분석하여 구현 범위를 결정한다.

**범위 분류:**

| 분류 | 조건 | 실행할 Phase |
|------|------|-------------|
| 풀스택 | 백엔드 API + 프론트엔드 UI 모두 필요 | 2 → 3 → 4 |
| 백엔드 전용 | API 구현만 필요 (프론트 없음) | 2 → 4 |
| 프론트엔드 전용 | 기존 API 활용, UI만 추가 | 3 → 4 |

요구사항을 `_workspace/00_requirements.md`에 저장한다:
```markdown
# 요구사항
## 기능 설명
## 범위: 풀스택 / 백엔드 전용 / 프론트엔드 전용
## 참고 도메인 (유사한 기존 구현)
## 특이사항

## Worktree
- 브랜치: feature/<name>
- 경로: C:/projects/northwind/.worktree/feature/<name>
```

**요구사항 저장 직후, Phase 2 시작 전에 반드시 git worktree를 생성한다:**

```bash
# 브랜치명: feature/<kebab-case-task-name>
# 예: feature/product-category, feature/order-management, feature/company-crm

# 신규 브랜치로 worktree 생성
git -C C:/projects/northwind worktree add .worktree/feature/<name> -b feature/<name>

# 브랜치가 이미 존재하면 기존 브랜치 재사용
git -C C:/projects/northwind worktree add .worktree/feature/<name> feature/<name>
```

- worktree 경로(`C:/projects/northwind/.worktree/feature/<name>`)를 `_workspace/00_requirements.md`의 `## Worktree` 섹션에 기록한다.
- **이후 모든 sub-agent에게는 이 worktree 경로를 `[프로젝트 루트]`로 전달한다. `C:/projects/northwind`를 직접 전달하지 않는다.**

---

## Phase 2: 백엔드 구현

`northwind-backend` 에이전트를 서브 에이전트로 호출한다.

```
Agent(
  subagent_type: "northwind-backend",
  model: "opus",
  prompt: """
    [요구사항]
    {Phase 1에서 정리한 요구사항}

    [작업 지시]
    1. `src/CLAUDE.md`를 읽어 컨벤션을 확인한다.
    2. 유사한 기존 도메인을 탐색하여 패턴을 파악한다.
    3. 백엔드 기능을 완전히 구현한다 (Entity → Repository → Converter → Service → [Business] → Controller → Validation → 테스트).
    4. 완료 후 `_workspace/02_backend_contract.md`를 작성한다.

    [프로젝트 루트]
    {_workspace/00_requirements.md의 Worktree 경로}
  """
)
```

에이전트 완료 후 `_workspace/02_backend_contract.md`가 생성됐는지 확인한다. 없으면 에이전트를 재호출한다.

---

## Phase 3: 프론트엔드 구현 (풀스택일 때만)

`northwind-frontend` 에이전트를 서브 에이전트로 호출한다.

```
Agent(
  subagent_type: "northwind-frontend",
  model: "opus",
  prompt: """
    [요구사항]
    {Phase 1에서 정리한 요구사항}

    [백엔드 API 계약]
    _workspace/02_backend_contract.md 파일을 읽어 사용한다.

    [작업 지시]
    1. `frontend/CLAUDE.md`를 읽어 컨벤션을 확인한다.
    2. 유사한 기존 페이지를 탐색하여 패턴을 파악한다.
    3. FSD 레이어 규칙을 준수하여 프론트엔드 기능을 완전히 구현한다.
    4. 완료 후 `_workspace/03_frontend_summary.md`를 작성한다.

    [프로젝트 루트]
    {_workspace/00_requirements.md의 Worktree 경로}
  """
)
```

---

## Phase 4: QA 검토

`northwind-qa` 에이전트를 서브 에이전트로 호출한다.

```
Agent(
  subagent_type: "northwind-qa",
  model: "opus",
  prompt: """
    [검토 대상]
    - 백엔드: _workspace/02_backend_contract.md 참고
    - 프론트엔드: _workspace/03_frontend_summary.md 참고 (있는 경우)

    [작업 지시]
    1. `src/CLAUDE.md`, `frontend/CLAUDE.md`를 읽어 컨벤션을 확인한다.
    2. 구현된 코드를 검토하고 위반 사항을 수정한다.
    3. 백엔드 테스트를 실행하여 통과 여부를 확인한다 (`.\gradlew.bat test`).
    4. `_workspace/04_qa_report.md`를 작성한다.

    [프로젝트 루트]
    {_workspace/00_requirements.md의 Worktree 경로}
  """
)
```

---

## Phase 5: 결과 보고

QA 리포트를 기반으로 사용자에게 다음을 보고한다:

1. **구현된 파일 목록** (백엔드 + 프론트엔드)
2. **API 엔드포인트** (경로, 메서드, 인증 여부)
3. **QA 결과** (PASS / CONDITIONAL PASS / FAIL)
4. **잔존 이슈** (있는 경우)
5. **브랜치**: `feature/<name>` — PR 생성 또는 `git worktree remove .worktree/feature/<name>`로 정리 가능

---

## 에러 핸들링

| 상황 | 대응 |
|------|------|
| 에이전트 산출물 파일 없음 | 해당 에이전트 1회 재호출 후, 재실패 시 누락 명시하고 계속 진행 |
| QA FAIL | QA 에이전트에게 수정 요청 후 재검토 1회 |
| 백엔드 테스트 실패 | northwind-backend에 실패 원인 전달하여 수정 요청 |

---

## 테스트 시나리오

### 정상 흐름
```
사용자: "order 도메인 기본 CRUD API와 관리자 페이지를 만들어줘"
→ Phase 1: 풀스택으로 분류, requirements 저장
→ Phase 2: Entity/Repo/Service/Controller/Test 생성, API 계약 저장
→ Phase 3: 타입 정의/페이지/라우터 구현
→ Phase 4: QA 통과 → PASS
→ Phase 5: 구현 목록 및 엔드포인트 보고
```

### 부분 재실행 흐름
```
사용자: "방금 만든 order API에서 검색 기능을 추가해줘"
→ Phase 0: _workspace/ 있음 + 부분 수정 요청 → northwind-backend만 재호출
→ Phase 4: QA 재검토
```

### 에러 흐름
```
백엔드 테스트 실패
→ QA가 실패 원인 분석 → northwind-backend에 수정 요청
→ 수정 후 테스트 재실행
→ PASS이면 계속, FAIL이면 이슈 명시하고 사용자에게 보고
```
