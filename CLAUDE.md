# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

- 백엔드 개발 컨벤션: [`src/CLAUDE.md`](src/CLAUDE.md)
- 프론트엔드 개발 컨벤션: [`frontend/CLAUDE.md`](frontend/CLAUDE.md)

## 프로젝트 개요

Northwind는 Microsoft의 클래식 Northwind 데이터베이스를 기반으로 한 풀스택 학습 프로젝트입니다. 백엔드는 Spring Boot 3.5(Java 21), 프론트엔드는 React 19 + Vite로 구성된 모노레포이며, Gradle이 프론트엔드를 빌드한 후 정적 자산으로 JAR에 번들링합니다.

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
