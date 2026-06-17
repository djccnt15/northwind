# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

- 백엔드 개발 컨벤션: [`src/CLAUDE.md`](src/CLAUDE.md)
- 프론트엔드 개발 컨벤션: [`frontend/CLAUDE.md`](frontend/CLAUDE.md)

## Critical Rules (중요 규칙)

- **git 작업**: 모든 작업은 `worktree`를 사용해서 별도의 작업 디렉토리를 만들어야 합니다.
- **DB 접근**: 프로덕션 DB에 직접 접근하거나 변경하는 코드를 작성하지 마세요. 모든 DB 작업은 애플리케이션 레이어를 통해 이루어져야 합니다.
- **보안**: API 키, 비밀번호, 개인 정보 등 민감한 정보는 절대 코드에 하드코딩하지 않습니다. `.env` 파일이나 환경 변수를 사용하여 관리해야 하고 절대 버전 관리 시스템에 커밋하지 않습니다.
- **코드 스타일**: 프로젝트의 코드 스타일 가이드를 준수해야 합니다. 백엔드는 Java 21의 표준 스타일을, 프론트엔드는 React 19 + Vite의 권장 스타일을 따릅니다.
- **테스트 작성**: 모든 새로운 기능과 버그 수정에는 적절한 단위 테스트와 통합 테스트가 포함되어야 합니다.
- **코드 리뷰**: 모든 코드 변경 사항은 최소한 한 명 이상의 팀원에 의해 리뷰되어야 합니다. 리뷰어는 코드의 품질, 일관성, 그리고 프로젝트의 아키텍처에 부합하는지 확인해야 합니다.

## 프로젝트 개요

Northwind는 Microsoft의 클래식 Northwind 데이터베이스를 기반으로 한 풀스택 학습 프로젝트입니다. 백엔드가 프론트엔드를 정적 리소스로 서빙하는 모노레포이며, Gradle이 프론트엔드를 빌드한 후 정적 자산으로 JAR에 번들링합니다.

- **백엔드**: Spring Boot 3.5, Java 21, Spring Data JPA, Spring Security, Lombok, springdoc-openapi
- **프론트엔드**: React 19, TypeScript 6, Vite 8, styled-components, react-router-dom, axios

### 아키텍처

```
docs/ -> 개발 기획 관련 문서
src/ -> 백엔드 애플리케이션 소스 코드
frontend/ -> 프론트엔드 애플리케이션 소스 코드
```

## 빌드 명령어

```powershell
# 전체 빌드 (프론트엔드 → 백엔드 순서)
.\gradlew.bat build

# 백엔드만 빌드 (프론트엔드 빌드 생략)
.\gradlew.bat build -x buildFrontend

# 프론트엔드만 빌드
cd frontend; npm run build   # 출력: src/main/resources/static/
```

## 로컬 실행

**백엔드** (localhost:3306에서 MySQL 8 실행 필요, 데이터베이스: `northwind`):
```powershell
.\gradlew.bat bootRun
```
앱은 `http://127.0.0.1:8080`에서 시작됩니다. Swagger UI는 `/swagger-ui/index.html`(ADMIN 권한 필요)에서 확인할 수 있습니다.

**프론트엔드 개발 서버** (별도 터미널):
```powershell
cd frontend; npm run dev   # http://localhost:5173
```

## API 공통 계약

### 응답 형식

모든 엔드포인트는 다음 형식으로 응답합니다:

```json
{
  "serverTime": 1234567890,
  "result": { "code": 200, "message": "Success", "description": "OK" },
  "body": { }
}
```

| code | 의미 |
|------|------|
| 200 | 성공 |
| 201 | 생성 성공 |
| 400 | 잘못된 요청 |
| 401 | 인증 필요 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 1200 | 세션 유효 (check-session 전용) |
| 1400 | 검증 오류 — `body`에 `{ field: message }` Map |

### 라우트 규칙

- `/api/public/v1/*` — 인증 불필요 (로그인, 회원가입, 헬스 체크)
- `/api/v1/*` — 세션 인증 필요

## 하네스: Northwind 풀스택 개발

**목표:** 백엔드(Spring Boot) + 프론트엔드(React FSD) 기능 개발을 에이전트 팀이 파이프라인으로 협업하여 프로젝트 컨벤션을 준수하며 구현.

**트리거:** 기능 구현, 도메인 추가, API 개발, 페이지 구현 등 Northwind 개발 작업 요청 시 `northwind-dev` 스킬을 사용하라. 단순 코드 설명이나 개념 질문은 직접 응답 가능.

**사전 설계 분기 (ad-hoc):** 요청이 기존 코드에서 따라갈 유사 패턴이 없는 "큰 구조 변경"(예: 새로운 공유 모듈/인프라 도입, ERD 스키마 변경, 여러 도메인에 걸친 cross-cutting 변경)에 해당하면, `northwind-dev` 호출 전에 `northwind-architect` 에이전트로 설계 방향을 사용자와 합의한 뒤 `northwind-dev`를 호출한다. 기존 패턴을 따르는 일반적인 CRUD/페이지 추가 등은 곧바로 `northwind-dev`를 사용한다.

**변경 이력:**

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

---

## Coding Convention

- **Git**
    - Git Flow 브랜칭 모델을 사용합니다. `main`은 항상 배포 가능한 상태로 유지하고, 기능 개발은 `feature/*` 브랜치에서 진행한 후 PR로 병합합니다.
    - 모든 커밋 메시지는 명확하고 간결해야 합니다. 특정 도메인 작업은 `task(domain): description` 형식을 사용합니다. 예: `feat(auth): add user authentication API`, `fix(product): resolve product listing error`, `refactor(order): simplify order status converter`. 여러 도메인에 걸친 변경이거나 프로젝트 전반에 해당하는 경우 domain 표기를 생략합니다. 예: `feat: add global error handler`, `refactor: unify API response format`.
    - 지원하는 타입: `feat`, `fix`, `bugfix`, `refactor`, `doc`, `test`, `git`.
    - 브랜치 이름은 `feature/`, `bugfix/`, `hotfix/` 등으로 시작해야 하며, 작업 내용을 간략히 설명해야 합니다. 예: `feature/user-authentication` 또는 `bugfix/product-listing-error`.

## Key Patterns (핵심 패턴)

- **테스트 주도 개발(TDD)**: 단위 테스트와 통합 테스트를 작성하여 코드의 안정성과 품질을 보장합니다. 백엔드는 JUnit/Mockito, 프론트엔드는 React Testing Library를 사용합니다.
- **컨벤셔널 커밋**: `task(domain): description` 형식으로 커밋 메시지를 작성하여 변경 도메인을 명확하게 기록합니다. 프로젝트 전반 변경은 domain 없이 `task: description`.
- **코드 리뷰**: PR을 통해 모든 변경 사항이 검토되고 승인되도록 하여 코드 품질과 팀 내 지식 공유를 촉진합니다.
