# 백엔드 API 계약 — i18n (국제화)

## 개요

다국어 지원 전략을 백엔드에 구현했다.
- **비인증 요청**: `AcceptHeaderLocaleResolver`(Accept-Language 헤더 기반) 폴백
- **인증 요청**: `UserSession.preferredLang` → 커스텀 `UserLocaleResolver`가 Locale 설정
- 로그인/check-session 응답에 `preferredLang` 추가
- 회원가입 시 `preferredLangCode`로 선호 언어 저장 (없으면 'en' 폴백)
- `GET /api/v1/lang`, `PATCH /api/v1/user/{userId}/lang` 신규 추가

## 생성된 파일

- `src/main/java/com/djccnt15/northwind/domain/lang/model/LangRes.java`
- `src/main/java/com/djccnt15/northwind/domain/lang/converter/LangConverter.java`
- `src/main/java/com/djccnt15/northwind/domain/lang/validation/LangErrorConst.java`
- `src/main/java/com/djccnt15/northwind/domain/lang/service/LangService.java`
- `src/main/java/com/djccnt15/northwind/domain/lang/controller/LangApiController.java`
- `src/main/java/com/djccnt15/northwind/domain/user/model/UpdateLangReq.java`
- `src/main/java/com/djccnt15/northwind/global/config/UserLocaleResolver.java`
- `src/test/java/com/djccnt15/northwind/domain/lang/service/LangServiceTest.java`
- `src/test/java/com/djccnt15/northwind/domain/lang/controller/LangApiControllerTest.java`
- `src/test/java/com/djccnt15/northwind/domain/user/business/UserBusinessLangTest.java`

## 수정된 파일

- `src/main/java/com/djccnt15/northwind/db/repository/SupportedLangRepo.java` — `findFirstByLang(String)` 추가
- `src/main/java/com/djccnt15/northwind/db/repository/AppUserRepo.java` — `findWithRoleFirstByUsername` / `findFullFirstById` EntityGraph에 `"preferredLang"` 추가
- `src/main/java/com/djccnt15/northwind/global/config/security/model/UserSession.java` — `String preferredLang` 필드 추가
- `src/main/java/com/djccnt15/northwind/global/config/security/AuthService.java` — `loadUserByUsername`에서 preferredLang 설정 (null → 'en' 폴백)
- `src/main/java/com/djccnt15/northwind/global/config/WebConfig.java` — `@Bean localeResolver()` 등록
- `src/main/java/com/djccnt15/northwind/domain/user/model/SessionInfoRes.java` — `preferredLang` 추가
- `src/main/java/com/djccnt15/northwind/domain/user/model/UserInfoRes.java` — `preferredLang` 추가
- `src/main/java/com/djccnt15/northwind/domain/user/model/SignupReq.java` — `preferredLangCode` 추가 (검증 없음)
- `src/main/java/com/djccnt15/northwind/domain/user/validation/AppUserModelConst.java` — `LANG_ID_NULL_ERR_MSG` 추가
- `src/main/java/com/djccnt15/northwind/domain/user/converter/UserConverter.java` — 두 `toResponse`에 `preferredLang` 매핑
- `src/main/java/com/djccnt15/northwind/domain/user/service/UserService.java` — `updateLang(AppUserEntity, SupportedLangEntity)` 추가
- `src/main/java/com/djccnt15/northwind/domain/user/business/UserBusiness.java` — `LangService` 의존성, `createUser` 언어 할당, `updateLang(...)` 신규
- `src/main/java/com/djccnt15/northwind/domain/user/controller/UserApiController.java` — `PATCH {userId}/lang` 추가
- `src/main/resources/messages.properties` / `messages_ko.properties` — `validation.appUser.preferredLangId.notNull`
- `src/main/resources/errors.properties` / `errors_ko.properties` — `error.lang.notFound`
- `src/test/resources/data-h2.sql` — `supported_lang` ('en','ko') 시드 추가 (맨 앞)

## API 엔드포인트

| 메서드 | 경로 | 인증 | 요청 바디 | 응답 바디 |
|--------|------|------|----------|----------|
| GET | `/api/v1/lang` | 필요 (`isAuthenticated()`) | - | `List<LangRes>` |
| PATCH | `/api/v1/user/{userId}/lang` | 필요 (`isAuthenticated()`, 본인만) | `UpdateLangReq` | `UserInfoRes` |
| GET | `/api/v1/auth/check-session` | 필요 | - | `SessionInfoRes` (preferredLang 추가됨) |
| POST | `/api/v1/auth/login/success` | 필요 | - | `SessionInfoRes` (preferredLang 추가됨) |
| POST | `/api/public/v1/user` (회원가입, 기존) | 불필요 | `SignupReq` (preferredLangCode 추가됨) | `UserInfoRes` (preferredLang 추가됨) |

> 모든 응답은 공통 `Api<T>` 래퍼: `{ serverTime, result: { code, message, description }, body }`.
> 검증 실패는 `result.code = 1400`, body에 `{ field: message }` Map.

## 응답/요청 타입 정의 (프론트엔드 타입 정의용)

### LangRes
| 필드 | 타입 | 설명 |
|------|------|------|
| id | number (Long) | 언어 ID |
| lang | string | 언어 코드 (예: "en", "ko") |

### UpdateLangReq (요청)
| 필드 | 타입 | 검증 | 설명 |
|------|------|------|------|
| preferredLangId | number (Long) | `@NotNull` (group: UpdateLang) | 변경할 언어 ID |

### SessionInfoRes (변경 — preferredLang 추가)
| 필드 | 타입 | 설명 |
|------|------|------|
| id | number (Long) | 사용자 ID |
| username | string | 사용자명 |
| authorities | string[] | 권한 목록 |
| preferredLang | string \| null | 선호 언어 코드 (세션 기준, 미설정 시 'en') |

### UserInfoRes (변경 — preferredLang 추가)
| 필드 | 타입 | 설명 |
|------|------|------|
| id | number (Long) | |
| username | string | |
| email | string \| null | |
| authorities | string[] | |
| isEnabled | boolean | |
| liveUntil | string (LocalDateTime) \| null | |
| passwordChangedAt | string (LocalDateTime) \| null | |
| loginFailedCount | number | |
| lastLoginAt | string (LocalDateTime) \| null | |
| team | string \| null | 팀명 |
| preferredLang | string \| null | 선호 언어 코드 (DB 미설정 시 null) |
| employee | EmployeeRes \| null | |

### SignupReq (변경 — preferredLangCode 추가, 회원가입 요청)
| 필드 | 타입 | 설명 |
|------|------|------|
| ... 기존 필드 ... | | |
| preferredLangCode | string \| null | 브라우저 언어 코드. null/공백/미지원 코드 → 'en' 폴백 |

## 에러 응답

| 상황 | code | message 키 |
|------|------|-----------|
| 존재하지 않는 언어 ID로 변경 (`getLang`) | 404 | `error.lang.notFound` |
| 본인이 아닌 userId로 lang 변경 | 400 | `error.user.unauthorizedAction` |
| `preferredLangId` 누락 | 1400 | `{ preferredLangId: "선호 언어는 필수입니다" }` |

## 동작/설계 노트

- **`UserLocaleResolver`**: `@Component` 없는 Plain class. `WebConfig`의 `@Bean localeResolver()`로 등록(Spring MVC가 빈 이름 `localeResolver`를 자동 탐지). `resolveLocale`은 SecurityContext의 `UserSession.preferredLang`을 우선 사용하고, 비인증/null/공백이면 `AcceptHeaderLocaleResolver`로 폴백 → NPE 없음. `setLocale`은 no-op.
- **`LangService.getLangOrDefault`**: null/공백/미지원 코드는 모두 'en'(`DEFAULT_LANG`) 폴백. 'en'이 DB에 없으면 `ApiException(NOT_FOUND)`.
- **회원가입 트랜잭션**: `UserBusiness.createUser`(`@Transactional`) 내부에서 역할 부여 → `langService.getLangOrDefault` → `userService.updateLang` 순서로 처리되어 롤백 경계 안에 포함.
- **언어 변경 즉시 반영**: `UserBusiness.updateLang`에서 `userSession.setPreferredLang(langEntity.getLang())`로 현재 세션 즉시 갱신.
- **EntityGraph**: `findWithRoleFirstByUsername`/`findFullFirstById`에 `"preferredLang"` 추가로 로그인·프로필 조회 시 LAZY N+1 방지.

## 테스트

| 테스트 | 종류 | 검증 |
|--------|------|------|
| `LangServiceTest` | `@SpringBootTest` | getLangs/getLang/notFound/getDefaultLang/getLangOrDefault(valid/null/blank/unknown 폴백) |
| `LangApiControllerTest` | `@WebMvcTest` | GET /api/v1/lang 정상(200) + 비인증(401) |
| `UserBusinessLangTest` | `@SpringBootTest` | createUser 언어 할당(ko/null폴백/unknown폴백), updateLang(세션 반영/notFound/userId 불일치) |

- H2(test 프로파일) 기준 전체 196개 테스트 통과.
- 실패한 15개는 `sampledata.*CreateTest`(`@DevTest`)로, 실제 MySQL `dev` DB가 필요한 시드 테스트이며 본 변경과 무관한 환경 의존 실패(컨텍스트 로드 실패)이다.
- 기존 `AuthApiControllerTest`(check-session)는 그대로 통과 — `SessionInfoRes.preferredLang` 추가는 가산적이며 `MessageUtil`이 모킹됨.

## doc/ 영향 (Phase 5 doc sub-agent 참고)

- `doc/StoryBoard.md` S-03(회원가입 `preferredLangCode`), S-11(`GET /api/v1/lang` + `PATCH /api/v1/user/{userId}/lang` "선호 언어 변경").
- `doc/PRD.md`, `doc/EDR.md`: 추가 변경 없음.
