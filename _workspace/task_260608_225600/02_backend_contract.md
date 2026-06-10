# 백엔드 구현 계약

## 개요
DTO 검증 메시지와 예외 응답 메시지에 i18n을 적용하고, 에러 메시지 키 문자열을 하드코딩 없이 `*ErrorConst` 상수로 추출했다. API 응답 포맷·상태 코드는 변경되지 않으며, 메시지 텍스트만 `Accept-Language`(현재 로케일)에 따라 영어/한국어로 응답된다.

## 신규 컴포넌트

### `MessageUtil` (`global/message/MessageUtil.java`)
`MessageSource` + `LocaleContextHolder.getLocale()`을 감싼 `@Component`. `messageUtil.getMessage(KEY)`로 현재 로케일에 맞는 메시지를 조회한다. `@RequiredArgsConstructor` + `private final MessageUtil messageUtil;`로 주입받아 사용.

## 메시지 번들 연동
- `spring.messages.basename: messages,errors` (일반 메시지 / 에러 메시지 basename 분리), `encoding: UTF-8`, `fallback-to-system-locale: false`
- `messages.properties`/`messages_ko.properties`(검증 메시지), `errors.properties`/`errors_ko.properties`(예외 응답 메시지)에 키-값 추가

## *ErrorConst 신규 클래스 (13개)
모두 `@UtilityClass` + `public static final String ..._ERR_MSG = "error.xxx..."` (평문 키, `{}` 미사용), `*ModelConst`와 동일한 `domain/<domain>/validation/` 패키지에 위치 (단, `auth`/`global` 예외):

| 클래스 | 패키지 | 상수 |
|---|---|---|
| `AppUserErrorConst` | `domain/user/validation` | `NOT_FOUND`, `UNAUTHORIZED_ACTION`, `PASSWORD_MISMATCH`, `USERNAME_DUPLICATE`, `EMAIL_DUPLICATE` |
| `EmployeeErrorConst` | `domain/user/validation` | `TITLE_REFERENCE_NOT_FOUND` |
| `UserRoleErrorConst` | `domain/role/validation` | `CANNOT_MODIFY_SUPER_ADMIN`, `CONTACT_ADMINISTRATOR` |
| `CompanyErrorConst` | `domain/company/validation` | `NOT_FOUND`, `NAME_DUPLICATE` |
| `CompanyTypeErrorConst` | `domain/company/validation` | `NOT_FOUND` |
| `ContactErrorConst` | `domain/company/validation` | `NOT_FOUND`, `EMAIL_DUPLICATE` |
| `ProductErrorConst` | `domain/product/validation` | `NOT_FOUND`, `CODE_DUPLICATE`, `NAME_DUPLICATE` |
| `ProductCategoryErrorConst` | `domain/product/validation` | `NOT_FOUND`, `NAME_DUPLICATE`, `CODE_DUPLICATE`, `HAS_PRODUCTS` |
| `TaxStatusErrorConst` | `domain/tax/validation` | `NOT_FOUND` |
| `TeamErrorConst` | `domain/team/validation` | `NOT_FOUND`, `NAME_DUPLICATE` |
| `TitleErrorConst` | `domain/title/validation` | `NOT_FOUND`, `DUPLICATE` |
| `AuthErrorConst` (신규 패키지 `domain/auth/validation`) | `domain/auth/validation` | `AUTHENTICATION_REQUIRED`, `ACCESS_DENIED`, `BAD_CREDENTIALS`, `ACCOUNT_DISABLED`, `ACCOUNT_LOCKED`, `ACCOUNT_EXPIRED`, `CREDENTIALS_EXPIRED`, `CONTACT_ADMINISTRATOR` |
| `GlobalErrorConst` (도메인 비종속 핸들러 메시지) | `global/exception` | `VALIDATION_FAILED`, `RESOURCE_NOT_FOUND`, `ACCESS_DENIED`, `UNEXPECTED` |

(상수명은 모두 `..._ERR_MSG` 접미사 — 표 가독성을 위해 생략)

## 변경된 기존 파일
- **Service** (16개): `UserService`, `EmployeeService`, `UserRoleService`, `RoleService`, `CompanyService`, `CompanyTypeService`, `ContactService`, `ProductService`, `ProductCategoryService`, `TaxStatusService`, `TeamService`, `TitleService`, `AuthService` — `messageUtil.getMessage(...)` 호출 시 `*ErrorConst` 상수를 `static import`로 사용하도록 변경
- **Controller**: `AuthPublicApiController`
- **예외 핸들러** (`global/exception/advice/`): `ApiExceptionHandler`, `AuthExceptionHandler`, `GlobalExceptionHandler` — `MessageUtil` 주입 + i18n 키 기반 응답으로 전환
- `AppUserModelConst` — 잘못 위치했던 `USERNAME_DUPLICATE_ERR_MSG`/`EMAIL_DUPLICATE_ERR_MSG`를 `AppUserErrorConst`로 이동(제거)

## 영향받는 API
- 모든 `/api/*`, `/api/public/*` 엔드포인트의 `Api.result.description` 및 검증 오류 응답(code 1400, `body`의 필드별 메시지 Map)이 로케일에 따라 영어/한국어로 응답됨
- 응답 JSON 스키마, 상태 코드, 라우트는 변경 없음 — 메시지 텍스트만 다국어화

## 테스트
- `@WebMvcTest` 슬라이스 5개 클래스에 `@MockitoBean private MessageUtil messageUtil;` 추가 (전역 등록된 `@RestControllerAdvice`가 `MessageUtil`을 transitively 의존하게 되어 슬라이스 컨텍스트에 빈이 없으면 `NoSuchBeanDefinitionException` 발생): `AdminProductCategoryApiControllerTest`, `AuthApiControllerTest`, `CompanyApiControllerTest`, `HealthApiControllerTest`, `ProductApiControllerTest`
- `src/test/resources/application.yaml`에 `spring.messages` 설정 블록 추가 — 해당 파일이 `src/main/resources/application.yaml`을 완전히 오버라이드하기 때문에 `basename: messages,errors`가 없으면 `NoSuchMessageException` 발생
- 테스트 실행 결과: `./gradlew.bat test -x buildFrontend` → `BUILD SUCCESSFUL`, 전체 테스트 통과
</content>
