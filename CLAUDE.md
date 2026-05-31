# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

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

**프론트엔드 개발 서버** (별도 터미널, API를 백엔드로 프록시):
```powershell
cd frontend; npm run dev   # http://localhost:5173
```

## 테스트

```powershell
# 전체 테스트 실행
.\gradlew.bat test

# 단일 테스트 클래스 또는 메서드 실행
.\gradlew.bat test --tests "com.djccnt15.northwind.domain.auth.controller.AuthPublicApiControllerTest.checkSession"

# JaCoCo 커버리지 리포트 포함 (출력: build/reports/jacoco/test/html/index.html)
.\gradlew.bat test jacocoTestReport
```

테스트는 H2 인메모리 데이터베이스를 사용합니다. 테스트 데이터는 `src/test/resources/data-h2.sql`에서 로드됩니다. 프로파일별 설정은 `src/test/resources/application-dev.yaml`에 있습니다.

## 아키텍처

### 백엔드 레이어 구조

각 기능은 `src/main/java/com/djccnt15/northwind/domain/<feature>/` 아래 도메인 모듈로 구성됩니다:
- `controller/` — REST 엔드포인트
- `service/` — 비즈니스 로직
- `converter/` — 엔티티 ↔ 모델 매핑
- `model/` — 요청/응답 DTO
- `validation/` — 입력 유효성 검사기 및 상수

공유 데이터 레이어는 `db/` 아래에 위치합니다:
- `entity/` — JPA 엔티티
- `repository/` — Spring Data JPA 리포지토리
- `projection/` — 최적화된 쿼리를 위한 DTO 프로젝션

공통 관심사는 `global/`에 있습니다:
- `api/` — 모든 엔드포인트에서 사용하는 통합 `Api<T>` 응답 래퍼
- `exception/` — 전역 예외 핸들러
- `config/security/` — Spring Security (폼 로그인, remember-me, RBAC, 사용자당 단일 세션)
- `constants/` — 라우트 상수
- `code/` — `Api<T>` 응답에 사용되는 상태 코드

### API 응답 형식

모든 엔드포인트는 다음 형식으로 응답합니다:
```json
{
  "serverTime": 1234567890,
  "result": { "status": "OK", "code": "200", "description": "Success" },
  "body": { }
}
```

### API 라우트 규칙

- `/api/v1/public/*` — 인증 불필요 (로그인, 회원가입, 헬스 체크)
- `/api/v1/*` — 세션 인증 필요

### 프론트엔드 구조

`frontend/src/` 아래 Feature-Sliced Design을 따릅니다:
- `app/` — 라우터, 프로바이더, 전역 설정
- `pages/` — 페이지 수준 컴포넌트
- `features/` — 기능 모듈
- `entities/` — 공유 데이터 타입
- `widgets/` — 재사용 가능한 복합 컴포넌트
- `shared/` — 유틸리티, 상수, API 클라이언트(Axios)

### 설정 프로파일

| 프로파일 | 용도 |
|---------|------|
| `dev` | MySQL 8 데이터베이스, 기본 활성 |
| `local` | 서버 포트/호스트 설정 |
| `test` | 테스트용 H2 인메모리 DB |
| `sql` | SQL 쿼리 로깅 활성화 |
| `prod` | 프로덕션 설정 |

개발 DB 자격증명은 `src/main/resources/application-dev.yaml`에 있습니다. 기본 사용자 비밀번호는 `1234`입니다.

### 보안 참고사항

- BCrypt 비밀번호 인코딩; 기본 비밀번호는 `application.yaml`에 설정
- Remember-me: 14일 토큰 유효 기간
- 세션 타임아웃: 2시간
- 로그인 실패 잠금: 6회 시도
- CSRF는 현재 비활성화됨 (프로덕션용 TODO로 표시)
- 중복 로그인 방지: 사용자당 하나의 활성 세션만 허용
- Swagger UI는 ADMIN 권한이 있는 사용자만 접근 가능

### 개발 참고사항

- API 응답은 `Api<T>` 래퍼로 일관되게 포맷팅되어 클라이언트에서 쉽게 처리할 수 있습니다.
- 프론트엔드는 React 19 + Vite로 빌드되어 빠른 개발 경험을 제공합니다. Gradle이 프론트엔드를 빌드한 후 정적 자산으로 JAR에 번들링하여 단일 배포 아티팩트를 생성합니다.
