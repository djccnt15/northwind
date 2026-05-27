# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Northwind is a full-stack study project based on the classic Microsoft Northwind database. Backend is Spring Boot 3.5 (Java 21), frontend is React 19 + Vite, built as a monorepo where Gradle builds the frontend and bundles it into the JAR as static assets.

## Build Commands

```powershell
# Full build (frontend then backend)
.\gradlew.bat build

# Backend only (skip frontend build)
.\gradlew.bat build -x buildFrontend

# Frontend only
cd frontend; npm run build   # outputs to src/main/resources/static/
```

## Running Locally

**Backend** (requires MySQL 8 running on localhost:3306, database `northwind`):
```powershell
.\gradlew.bat bootRun
```
App starts at `http://127.0.0.1:8080`. Swagger UI at `/swagger-ui/index.html` (ADMIN role only).

**Frontend dev server** (separate terminal, proxies API to backend):
```powershell
cd frontend; npm run dev   # http://localhost:5173
```

## Testing

```powershell
# All tests
.\gradlew.bat test

# Single test class or method
.\gradlew.bat test --tests "com.djccnt15.northwind.domain.auth.controller.AuthPublicApiControllerTest.checkSession"

# With JaCoCo coverage report (output: build/reports/jacoco/test/html/index.html)
.\gradlew.bat test jacocoTestReport
```

Tests use H2 in-memory database. Test data is loaded from `src/test/resources/data-h2.sql`. Profile-specific config is in `src/test/resources/application-dev.yaml`.

## Architecture

### Backend Layer Structure

Each feature is organized as a domain module under `src/main/java/com/djccnt15/northwind/domain/<feature>/`:
- `controller/` — REST endpoints
- `service/` — business logic
- `converter/` — entity ↔ model mapping
- `model/` — request/response DTOs
- `validation/` — input validators and constants

Shared data layer lives under `db/`:
- `entity/` — JPA entities
- `repository/` — Spring Data JPA repositories
- `projection/` — DTO projections for optimized queries

Cross-cutting concerns are in `global/`:
- `api/` — unified `Api<T>` response wrapper used by all endpoints
- `exception/` — global exception handlers
- `config/security/` — Spring Security (form login, remember-me, RBAC, single session per user)
- `constants/` — route constants
- `code/` — status codes used in `Api<T>` responses

### API Response Format

All endpoints return:
```json
{
  "serverTime": 1234567890,
  "result": { "status": "OK", "code": "200", "description": "Success" },
  "body": { }
}
```

### API Route Conventions

- `/api/v1/public/*` — unauthenticated (login, signup, health check)
- `/api/v1/*` — requires session authentication

### Frontend Structure

Follows Feature-Sliced Design under `frontend/src/`:
- `app/` — router, providers, global setup
- `pages/` — page-level components
- `features/` — feature modules
- `entities/` — shared data types
- `widgets/` — reusable composite components
- `shared/` — utilities, constants, API client (Axios)

### Configuration Profiles

| Profile | Purpose |
|---------|---------|
| `dev` | MySQL 8 database, active by default |
| `local` | Server port/host settings |
| `test` | H2 in-memory DB for tests |
| `sql` | Enables SQL query logging |
| `prod` | Production settings |

Dev DB credentials are in `src/main/resources/application-dev.yaml`. Default user password is `1234`.

### Security Notes

- BCrypt password encoding; default password configured in `application.yaml`
- Remember-me: 14-day token validity
- Session timeout: 2 hours
- Login failure lockout: 6 attempts
- CSRF is currently disabled (marked TODO for production)
- Duplicate login prevention: only one active session per user
