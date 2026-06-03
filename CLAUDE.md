# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

- 백엔드 개발 컨벤션: [`src/CLAUDE.md`](src/CLAUDE.md)
- 프론트엔드 개발 컨벤션: [`frontend/CLAUDE.md`](frontend/CLAUDE.md)

## Critical Rules (중요 규칙)

- **git 작업**: 모든 작업은 `worktree`를 사용해서 별도의 작업 디렉토리를 만들어야 합니다.
- **DB 접근**: production 데이터베이스에는 절대 접근하지 않습니다. 로컬에서 MySQL 8을 실행하고 `northwind` 데이터베이스를 사용해야 합니다.
- **보안**: API 키, 비밀번호, 개인 정보 등 민감한 정보는 절대 코드에 하드코딩하지 않습니다. `.env` 파일이나 환경 변수를 사용하여 관리해야 하고 절대 버전 관리 시스템에 커밋하지 않습니다.
- **코드 스타일**: 프로젝트의 코드 스타일 가이드를 준수해야 합니다. 백엔드는 Java 21의 표준 스타일을, 프론트엔드는 React 19 + Vite의 권장 스타일을 따릅니다.
- **테스트 작성**: 모든 새로운 기능과 버그 수정에는 적절한 단위 테스트와 통합 테스트가 포함되어야 합니다.
- **코드 리뷰**: 모든 코드 변경 사항은 최소한 한 명 이상의 팀원에 의해 리뷰되어야 합니다. 리뷰어는 코드의 품질, 일관성, 그리고 프로젝트의 아키텍처에 부합하는지 확인해야 합니다.

## 프로젝트 개요

Northwind는 Microsoft의 클래식 Northwind 데이터베이스를 기반으로 한 풀스택 학습 프로젝트입니다. 백엔드는 Spring Boot 3.5(Java 21), 프론트엔드는 React 19 + Vite로 구성된 모노레포이며, Gradle이 프론트엔드를 빌드한 후 정적 자산으로 JAR에 번들링합니다.

### 아키텍처

```
src/ -> 백엔드 프로젝트
frontend/ -> 프론트엔드 프로젝트
doc/ -> 기획 및 설계 자료
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

- `/api/v1/public/*` — 인증 불필요 (로그인, 회원가입, 헬스 체크)
- `/api/v1/*` — 세션 인증 필요

## Coding Convention

- **Git**
    - Git Flow 브랜칭 모델을 사용합니다. `main`은 항상 배포 가능한 상태로 유지하고, 기능 개발은 `feature/*` 브랜치에서 진행한 후 PR로 병합합니다.
    - 모든 커밋 메시지는 명확하고 간결해야 합니다. 예: `feat: Add user authentication API` 또는 `fix: Resolve issue with product listing`.
    - 브랜치 이름은 `feature/`, `bugfix/`, `hotfix/` 등으로 시작해야 하며, 작업 내용을 간략히 설명해야 합니다. 예: `feature/user-authentication` 또는 `bugfix/product-listing-error`.
