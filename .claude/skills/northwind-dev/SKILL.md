---
name: northwind-dev
description: >
  Northwind 풀스택 기능 개발을 에이전트 팀으로 조율하는 오케스트레이터.
  Phase 0(컨텍스트 확인) → Phase 1(작업 계획 수립 + worktree 생성) → Phase 2(백엔드) →
  Phase 3(프론트엔드) → Phase 4(QA) → Phase 5(문서 반영) → Phase 6(결과 보고) 순으로 수행한다.
  "기능 추가", "도메인 구현", "API 개발", "페이지 구현", "풀스택 개발", "백엔드 추가", "프론트 구현",
  "재구현", "다시 만들어", "수정", "보완", "업데이트", "개선" 등 Northwind 개발 요청이 오면 반드시 이 스킬을 사용하라.
  단순 코드 설명이나 개념 질문은 직접 응답해도 된다.
---

## 실행 모드

**서브 에이전트 파이프라인** — Phase 1(계획 수립 + worktree 생성)은 오케스트레이터가 대화 맥락을 가진 채 직접 수행하고, Phase 2 이후는 각 Phase가 독립적 산출물을 파일로 전달하며 서브 에이전트가 순차 실행한다.

```
오케스트레이터
 ├── Phase 0: 컨텍스트 확인
 ├── Phase 1: 작업 계획 수립 + Worktree 생성 (오케스트레이터 직접 수행)
 ├── Phase 2: 백엔드 구현 (northwind-backend sub-agent)
 ├── Phase 3: 프론트엔드 구현 (northwind-frontend sub-agent) — 풀스택일 때만
 ├── Phase 4: QA 검토 (northwind-qa sub-agent)
 ├── Phase 5: 문서 반영 (northwind-doc sub-agent)
 └── Phase 6: 결과 보고
```

> **`_workspace/task_*/` 산출물 위치**: 모든 Phase의 산출물(`00_requirements.md` ~ `05_doc_update.md`)은 `C:/projects/northwind/_workspace/`(main)가 아니라 **worktree 내부** `{WORKTREE_PATH}/_workspace/{TASK_NAME}/`에 생성한다. 이렇게 하면 산출물이 feature 브랜치 커밋·PR에 자연스럽게 포함되어 main에 별도로 정리할 필요가 없다 (2026-06-12 변경 — 이전에는 main의 `_workspace/`에 생성되어 PR 머지 후 별도 커밋으로 main에 수동 반영해야 했음).

---

## ⚠️ 오케스트레이터 제약 (중요)

이 스킬을 실행하는 에이전트(오케스트레이터)는 **구현 코드와 기획 문서(doc/)를 직접 작성·수정하지 않는다.** 실제 구현/검증/문서 반영은 반드시 서브 에이전트(`northwind-backend`, `northwind-frontend`, `northwind-qa`, `northwind-doc`)에게 위임한다.

- 오케스트레이터의 역할은 **TASK_NAME 결정 → 작업 계획 수립(직접) → worktree 생성 → 서브 에이전트 호출 → 산출물 확인 → 결과 보고**로 한정된다.
- **예외적으로 오케스트레이터가 직접 수행하는 것**:
  - **Phase 1 작업 계획 수립** — `src/`, `frontend/src/`, `doc/`를 `Read`/`Grep`/`Glob`으로 조사하고, worktree 생성 후 `{TASK_DIR}/00_requirements.md`, `01_plan.md`를 직접 작성한다 (`{TASK_DIR}` = `{WORKTREE_PATH}/_workspace/{TASK_NAME}/`). 이 두 파일은 `_workspace/` 산출물이며 구현 코드/기획 문서가 아니므로 직접 작성 대상이다.
  - TASK_NAME 결정(Phase 0), git worktree 생성/정리(Phase 1), 서브 에이전트 호출 및 산출물 존재 확인, 결과 보고(Phase 6)
- "간단해 보이는 수정"이라도 오케스트레이터가 `Edit`/`Write`로 `src/main`, `src/test`, `frontend/src`, `doc/` 등을 직접 변경하지 않는다 — 사소해 보이는 변경도 컨벤션 누락(예: `*ErrorConst` 분리, `@MockitoBean` 보강 등)이나 테스트 누락, 문서 동기화 누락으로 이어질 수 있으므로 전문 서브 에이전트의 점검을 거쳐야 한다.
- 직접 `.\gradlew.bat test`, `npm run build` 등을 실행해 "검증까지 끝났다"고 판단하지 않는다 — 검증은 Phase 4의 `northwind-qa` 책임이다.

> 이 규칙이 필요했던 이유: i18n 적용 작업(`feature/backend-i18n-validation-error`)에서 오케스트레이터가 Phase 2/4를 건너뛰고 직접 13개 `*ErrorConst` 클래스 생성, 16개 서비스/컨트롤러/핸들러 수정, 테스트 보강까지 전부 수행한 뒤 산출물(`_workspace/*.md`) 없이 완료 보고만 한 사례가 있었음.

> Phase 5(`northwind-doc`)가 필요했던 이유: Phase 1~4 완료 후 main에 머지되었음에도 `doc/StoryBoard.md`의 화면별 구현 상태(✅/🔲)가 갱신되지 않아, S-30/31/32·S-60/61/62가 실제로는 구현 완료됐는데도 문서에는 🔲로 남아있던 사례(2026-06-10 수동 정정)가 있었음. 문서 동기화를 파이프라인의 정규 단계로 만들어 재발을 방지한다.

> Phase 1을 오케스트레이터가 직접 수행하는 이유: 별도 `northwind-planner` 서브 에이전트로 분리했더니, 사용자와의 대화 맥락(이전 논의·의도·뉘앙스)이 서브 에이전트에 전달되지 않아 계획이 사용자 의도와 어긋날 위험이 있었음. 계획 수립을 오케스트레이터가 대화 맥락을 그대로 유지한 채 직접 수행하도록 통합(2026-06-10).

---

## Phase 0: 컨텍스트 확인

`C:/projects/northwind/.worktree/*/_workspace/task_*/` 를 `Glob`으로 확인하여 진행 중인 작업이 있는지 확인한다 (예: `.worktree/feature/order-management/_workspace/task_260612_164323/`).

- **새 기능 요청** → 현재 시각 기준 `TASK_NAME = task_{YYMMDD}_{HHMMSS}` (예: `task_260610_143022`)로 지정한다. 실제 디렉토리는 Phase 1에서 worktree 생성 후 `{WORKTREE_PATH}/_workspace/{TASK_NAME}/`에 만든다 — 이 시점에는 아직 생성하지 않는다.
- **"수정/보완" 요청 (직전 작업 연속)** → 위에서 찾은 worktree들 중 폴더명 정렬 기준 가장 최근 `task_*/` 폴더를 `TASK_NAME`/worktree로 재사용한다. 해당 `00_requirements.md`에서 `WORKTREE_PATH`를 확인하고(이미 그 worktree 안에 있으므로 경로 자체로 확인 가능), 변경 범위에 해당하는 Phase만 재수행하여 같은 디렉토리에 산출물을 갱신한다. 일치하는 worktree를 찾지 못하면 새 기능 요청으로 취급한다.

이후 모든 Phase에서 언급하는 `{TASK_DIR}`은 `{WORKTREE_PATH}/_workspace/{TASK_NAME}/`을 가리킨다 (worktree 내부 경로). sub-agent에게는 `[프로젝트 루트]`(= `{WORKTREE_PATH}`) 기준 경로인 `_workspace/{TASK_NAME}/파일명.md` 또는 이를 풀어쓴 절대경로 `{WORKTREE_PATH}/_workspace/{TASK_NAME}/파일명.md`로 전달한다 — main의 `C:/projects/northwind/_workspace/`와 혼동하지 않는다.

---

## Phase 1: 작업 계획 수립 + Worktree 생성 (오케스트레이터 직접 수행)

사용자 요청을 분석하여 이후 모든 Phase(백엔드/프론트엔드/QA/문서 반영)가 참고할 작업 계획을 직접 작성하고, 이를 저장할 worktree를 생성한다.

1. **요구사항 정리**: 기능 설명, 범위 분류
   - **풀스택**: 백엔드 API + 프론트엔드 UI 모두 필요 → 실행할 Phase: 2 → 3 → 4 → 5
   - **백엔드 전용**: API 구현만 필요 (프론트 없음) → 실행할 Phase: 2 → 4 → 5
   - **프론트엔드 전용**: 기존 API 활용, UI만 추가 → 실행할 Phase: 3 → 4 → 5
2. **참고 패턴 탐색**: `Glob`/`Grep`으로 유사한 기존 도메인(`src/main/java/.../domain/*`)·페이지(`frontend/src/pages/*`)를 찾아 구현 시 참고할 패턴을 정리한다.
3. **단계별 계획 수립**: 백엔드/프론트엔드 각 Phase에서 만들어야 할 레이어·파일을 구체적으로 나열한다.
4. **doc/ 영향 범위 파악**: `doc/StoryBoard.md`(필요 시 `doc/PRD.md`, `doc/EDR.md`)를 읽고, 이번 작업이 어떤 화면(S-XX)·섹션에 영향을 주는지 식별한다. 영향이 없으면 명시적으로 "없음"이라고 기록한다 — 추측으로 비워두지 않는다.
5. **Worktree 브랜치명 결정**: `feature/<kebab-case-name>` 형식.
6. **Worktree 생성** ("수정/보완"으로 기존 worktree를 재사용하는 경우 생략):

   ```bash
   # 브랜치명: feature/<kebab-case-task-name>
   # 예: feature/product-category, feature/order-management, feature/company-crm

   # 신규 브랜치로 worktree 생성
   git -C C:/projects/northwind worktree add .worktree/feature/<name> -b feature/<name>

   # 브랜치가 이미 존재하면 기존 브랜치 재사용
   git -C C:/projects/northwind worktree add .worktree/feature/<name> feature/<name>
   ```

   생성된 worktree 경로(`C:/projects/northwind/.worktree/feature/<name>`)를 `WORKTREE_PATH`로 확정한다. **이후 모든 sub-agent에게는 이 경로를 `[프로젝트 루트]`로 전달한다. `C:/projects/northwind`를 직접 전달하지 않는다.**
7. `{WORKTREE_PATH}/_workspace/{TASK_NAME}/00_requirements.md`, `01_plan.md`를 작성한다 (즉 `{TASK_DIR}/00_requirements.md`, `01_plan.md`).

"수정/보완" 요청(Phase 0에서 기존 worktree/`TASK_NAME` 재사용)인 경우, 6단계(worktree 생성)는 생략하고 기존 `00_requirements.md`/`01_plan.md`를 읽어 변경 범위만큼만 갱신한다.

### `{TASK_DIR}/00_requirements.md`

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

### `{TASK_DIR}/01_plan.md`

```markdown
# 구현 계획

## 백엔드 작업 항목
(풀스택/백엔드 전용일 때) Entity → Repository → Converter → Service → [Business] → Controller → Validation → Test
단위로 신규/수정 파일과 작업 내용을 구체적으로 나열

## 프론트엔드 작업 항목
(풀스택/프론트엔드 전용일 때) entities → features → pages → router 단위로 신규/수정 파일과 작업 내용을 구체적으로 나열

## 참고 패턴
- 기존 도메인/페이지 경로와 따라야 할 패턴 설명

## doc/ 영향 범위
- doc/StoryBoard.md: 영향받는 화면 ID(S-XX)와 변경 내용 (해당 없으면 "없음"과 그 이유)
- doc/PRD.md / doc/EDR.md: 영향 여부 (해당 없으면 "없음")

## QA 중점 검토 항목
- 경계면(백엔드 계약 ↔ 프론트엔드 타입) 등 특히 신경써야 할 지점
```

### Phase 1 에러 핸들링

- 유사 도메인을 찾을 수 없는 경우: 가장 가까운 패턴을 명시하고 차이점을 기록한다.
- doc/ 영향 범위가 모호한 경우: "없음"으로 단정하지 말고, 가능성 있는 화면 ID를 후보로 나열한다 (최종 판단은 Phase 5의 `northwind-doc`이 코드 기준으로 내린다).

---

## Phase 2: 백엔드 구현

`northwind-backend` 에이전트를 서브 에이전트로 호출한다.

```
Agent(
  subagent_type: "northwind-backend",
  model: "opus",
  prompt: """
    [요구사항 및 계획]
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/00_requirements.md`
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/01_plan.md` (백엔드 작업 항목, 참고 패턴)

    [작업 지시]
    1. `src/CLAUDE.md`를 읽어 컨벤션을 확인한다 (레이어 템플릿·i18n 체크리스트는 `northwind-backend-scaffold` 스킬 참고).
    2. `01_plan.md`의 백엔드 작업 항목과 참고 패턴을 바탕으로 유사한 기존 도메인을 탐색하여 패턴을 파악한다.
    3. 백엔드 기능을 완전히 구현한다 (Entity → Repository → Converter → Service → [Business] → Controller → Validation → 테스트).
    4. 완료 후 `{WORKTREE_PATH}/_workspace/{TASK_NAME}/02_backend_contract.md`를 작성한다.

    [프로젝트 루트]
    {WORKTREE_PATH}
  """
)
```

에이전트 완료 후 `{TASK_DIR}/02_backend_contract.md`가 생성됐는지 확인한다. 없으면 에이전트를 재호출한다.

---

## Phase 3: 프론트엔드 구현 (풀스택일 때만)

`northwind-frontend` 에이전트를 서브 에이전트로 호출한다.

```
Agent(
  subagent_type: "northwind-frontend",
  model: "opus",
  prompt: """
    [요구사항 및 계획]
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/00_requirements.md`
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/01_plan.md` (프론트엔드 작업 항목, 참고 패턴)

    [백엔드 API 계약]
    `{WORKTREE_PATH}/_workspace/{TASK_NAME}/02_backend_contract.md` 파일을 읽어 사용한다.

    [작업 지시]
    1. `frontend/CLAUDE.md`를 읽어 컨벤션을 확인한다 (DataGrid CRUD 템플릿은 `northwind-frontend-admin-crud` 스킬 참고).
    2. `01_plan.md`의 프론트엔드 작업 항목과 참고 패턴을 바탕으로 유사한 기존 페이지를 탐색하여 패턴을 파악한다.
    3. FSD 레이어 규칙을 준수하여 프론트엔드 기능을 완전히 구현한다.
    4. 구현 완료 후 `cd frontend; npm run build`로 빌드를 검증하고, 통과한 뒤에만 `{WORKTREE_PATH}/_workspace/{TASK_NAME}/03_frontend_summary.md`를 작성한다 (`npx tsc --noEmit` 단독 실행 금지 — 사유는 `northwind-frontend-admin-crud` 스킬 참고).

    [프로젝트 루트]
    {WORKTREE_PATH}
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
    - 백엔드: `{WORKTREE_PATH}/_workspace/{TASK_NAME}/02_backend_contract.md` 참고
    - 프론트엔드: `{WORKTREE_PATH}/_workspace/{TASK_NAME}/03_frontend_summary.md` 참고 (있는 경우)
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/01_plan.md`의 "QA 중점 검토 항목" 우선 확인

    [작업 지시]
    1. `src/CLAUDE.md`, `frontend/CLAUDE.md`를 읽어 컨벤션을 확인한다 (통합 체크리스트와 경계면 비교 절차는 `northwind-qa-boundary-check` 스킬 참고).
    2. 구현된 코드를 검토하고 위반 사항을 수정한다.
    3. 백엔드 테스트(`.\gradlew.bat test`)와 프론트엔드 빌드(`cd frontend; npm run build`)를 모두 실행하여 통과 여부를 확인한다 (`npx tsc --noEmit` 단독 실행 금지 — 사유는 `northwind-frontend-admin-crud` 스킬 참고).
    4. `{WORKTREE_PATH}/_workspace/{TASK_NAME}/04_qa_report.md`를 작성한다.

    [프로젝트 루트]
    {WORKTREE_PATH}
  """
)
```

---

## Phase 5: 문서 반영

`northwind-doc` 에이전트를 서브 에이전트로 호출한다.

```
Agent(
  subagent_type: "northwind-doc",
  model: "opus",
  prompt: """
    [참고 산출물]
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/00_requirements.md`
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/01_plan.md` (doc/ 영향 범위)
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/02_backend_contract.md` (있는 경우)
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/03_frontend_summary.md` (있는 경우)
    - `{WORKTREE_PATH}/_workspace/{TASK_NAME}/04_qa_report.md` (최종 판정 확인, 필수)

    [작업 지시]
    1. `04_qa_report.md`의 최종 판정이 PASS 또는 CONDITIONAL PASS인 경우에만 진행한다 (FAIL이면 문서 변경 없이 사유만 기록).
    2. `01_plan.md`의 doc/ 영향 범위를 라우터/컨트롤러 등 실제 코드로 교차 검증한다 (StoryBoard 섹션별 갱신 패턴은 `northwind-doc-storyboard-sync` 스킬 참고).
    3. `doc/StoryBoard.md`(영향 있는 경우 `doc/PRD.md`/`doc/EDR.md`)를 갱신한다.
    4. `{WORKTREE_PATH}/_workspace/{TASK_NAME}/05_doc_update.md`를 작성한다.

    [프로젝트 루트]
    {WORKTREE_PATH}
  """
)
```

에이전트 완료 후 `{TASK_DIR}/05_doc_update.md`가 생성됐는지 확인한다. 없으면 에이전트를 재호출한다.

---

## Phase 6: 결과 보고

QA 리포트와 문서 반영 결과를 기반으로 사용자에게 다음을 보고한다:

1. **구현된 파일 목록** (백엔드 + 프론트엔드)
2. **API 엔드포인트** (경로, 메서드, 인증 여부)
3. **QA 결과** (PASS / CONDITIONAL PASS / FAIL)
4. **문서 반영 결과** (`05_doc_update.md` 요약 — 변경된 doc/ 파일 또는 "변경 없음" 사유)
5. **잔존 이슈** (있는 경우)
6. **브랜치**: `feature/<name>` — PR 생성 또는 `git worktree remove .worktree/feature/<name>`로 정리 가능

> `_workspace/{TASK_NAME}/` 산출물은 `{WORKTREE_PATH}` 내부에 있으므로, 구현 코드와 함께 커밋·푸시하면 PR에 자동으로 포함된다. main 측에 별도로 복사하거나 추가 커밋할 필요가 없다.

---

## 에러 핸들링

| 상황 | 대응 |
|------|------|
| 유사 도메인/페이지를 찾을 수 없음 (Phase 1) | 가장 가까운 패턴을 명시하고 차이점을 `01_plan.md`에 기록 |
| doc/ 영향 범위가 모호함 (Phase 1) | "없음"으로 단정하지 않고 후보 화면 ID를 나열, 최종 판단은 Phase 5에 위임 |
| 에이전트 산출물 파일 없음 | 해당 에이전트 1회 재호출 후, 재실패 시 누락 명시하고 계속 진행 |
| QA FAIL | QA 에이전트에게 수정 요청 후 재검토 1회 |
| 백엔드 테스트 실패 | northwind-backend에 실패 원인 전달하여 수정 요청 |
| 프론트엔드 빌드 실패 | northwind-frontend에 실패 원인 전달하여 수정 요청 후 재검증 |
| QA가 최종적으로 FAIL | Phase 5(`northwind-doc`)는 호출하되 문서 변경 없이 보류 사유만 기록하도록 지시 |

---

## 테스트 시나리오

### 정상 흐름
```
사용자: "order 도메인 기본 CRUD API와 관리자 페이지를 만들어줘"
→ Phase 0: 새 기능 요청 → TASK_NAME = task_260610_143022로 지정
→ Phase 1: 오케스트레이터가 풀스택으로 분류 → 브랜치명 결정 → worktree 생성(.worktree/feature/order-management)
        → {WORKTREE_PATH}/_workspace/task_260610_143022/00_requirements.md + 01_plan.md 작성 (doc/ 영향: S-40/41/42)
→ Phase 2: Entity/Repo/Service/Controller/Test 생성, API 계약 저장
→ Phase 3: 타입 정의/페이지/라우터 구현
→ Phase 4: QA 통과 → PASS
→ Phase 5: doc/StoryBoard.md의 S-40/41/42 ✅ 갱신, 05_doc_update.md 작성
→ Phase 6: 구현 목록·엔드포인트·문서 반영 결과 보고 (모든 산출물은 worktree 내부 → 커밋 시 PR에 포함)
```

### 부분 재실행 흐름
```
사용자: "방금 만든 order API에서 검색 기능을 추가해줘"
→ Phase 0: .worktree/*/_workspace/task_*/ 중 가장 최근 폴더(및 해당 worktree)를 재사용
→ Phase 1: 변경 범위가 작으면 생략 가능, 범위가 바뀌면 오케스트레이터가 01_plan.md만 갱신 (worktree 재생성 불필요)
→ northwind-backend만 재호출
→ Phase 4: QA 재검토
→ Phase 5: doc/ 영향이 추가로 없으면 northwind-doc이 "변경 없음"으로 05_doc_update.md 갱신
```

### 에러 흐름
```
백엔드 테스트 실패
→ QA가 실패 원인 분석 → northwind-backend에 수정 요청
→ 수정 후 테스트 재실행
→ PASS이면 계속, FAIL이면 이슈 명시하고 사용자에게 보고
```
