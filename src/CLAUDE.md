# 백엔드 개발 참고사항 및 코딩 컨벤션

## 테스트 실행

```powershell
# 전체 테스트
.\gradlew.bat test

# 단일 테스트 클래스 또는 메서드
.\gradlew.bat test --tests "com.djccnt15.northwind.domain.auth.controller.AuthPublicApiControllerTest.checkSession"

# JaCoCo 커버리지 리포트 (출력: build/reports/jacoco/test/html/index.html)
.\gradlew.bat test jacocoTestReport
```

테스트 데이터: `src/test/resources/data-h2.sql` / 프로파일 설정: `src/test/resources/application-dev.yaml`

---

## 설정 프로파일

| 프로파일 | 용도 |
|---------|------|
| `dev` | MySQL 8 데이터베이스, 기본 활성 |
| `local` | 서버 포트/호스트 설정 |
| `test` | 테스트용 H2 인메모리 DB |
| `sql` | SQL 쿼리 로깅 활성화 |
| `prod` | 프로덕션 설정 |

개발 DB 자격증명: `src/main/resources/application-dev.yaml` / 기본 사용자 비밀번호: `1234`

---

## 패키지 구조

```
com.djccnt15.northwind/
├── db/
│   ├── entity/       JPA 엔티티
│   ├── repository/   Spring Data JPA 리포지토리
│   └── projection/   JOIN 결과 매핑용 인터페이스 프로젝션
├── domain/           도메인별 비즈니스 로직
│   └── <domain>/
│       ├── controller/
│       ├── business/  (선택적, 복잡한 도메인만)
│       ├── service/
│       ├── converter/
│       ├── model/     요청(Req) / 응답(Res) DTO
│       └── validation/ 검증 상수
└── global/
    ├── api/          Api<T>, Result 응답 래퍼
    ├── code/         StatusCode 열거형
    ├── constants/    RouteConst, RoleConst
    ├── annotation/   커스텀 어노테이션
    ├── config/       보안, JPA, Web 설정
    ├── aop/          AOP 포인트컷, 실행시간 추적
    ├── exception/    전역 예외 핸들러
    ├── filter/       서블릿 필터
    └── interceptor/  MVC 인터셉터
```

---

## 레이어 구조 및 책임

```
Controller → Business → Service → Repository
                ↕           ↕
            Converter    Entity
```

| 레이어 | 어노테이션 | 책임 |
|--------|-----------|------|
| Controller | `@RestController` | 요청/응답 매핑, 인증 어노테이션 |
| Business | `@Business` | 여러 Service 조합, `@Transactional` 경계 |
| Service | `@Service` | 도메인 규칙 검증, 단일 리포지토리 접근 |
| Converter | `@Converter` | Entity ↔ DTO 매핑만 담당 (비즈니스 로직 없음) |

- **Business 레이어**는 복잡한 도메인(`admin`, `user`)에만 존재. 단순 도메인(`team`, `title`)은 Controller가 Service를 직접 호출
- **`@Transactional`**은 Business 레이어에 작성. Service에는 달지 않음
- **의존성 주입**은 항상 `@RequiredArgsConstructor` + `private final` 필드 사용

---

## 커스텀 어노테이션

`global/annotation/`에 정의된 `@Service` 별칭 어노테이션을 사용한다.

| 어노테이션 | 대상 | 메타 어노테이션 |
|-----------|------|----------------|
| `@Business` | business 계층 클래스 | `@Service` |
| `@Converter` | converter 계층 클래스 | `@Service` |

- Utility 클래스는 `@UtilityClass` 사용

---

## API 응답 패턴

모든 엔드포인트는 `ResponseEntity<Api<T>>`를 반환한다.

```java
// 성공 (200)
return ResponseEntity.ok(Api.OK(response));

// 생성 (201)
return ResponseEntity.ok(Api.CREATED(response));

// 삭제 등 body 없는 응답
return ResponseEntity.ok(Api.OK(null));
```

**`Api<T>` 구조** (`global/api/Api.java`):

```json
{
  "serverTime": 1234567890,
  "result": { "code": 200, "message": "Success", "description": "OK" },
  "body": { }
}
```

---

## 예외 처리

**예외 발생**: `ApiException`에 `StatusCode`를 담아 던진다.

```java
// NOT_FOUND
throw new ApiException(StatusCode.NOT_FOUND, "Title not found");

// BAD_REQUEST
throw new ApiException(StatusCode.BAD_REQUEST, "Team name already exists");

// UNAUTHORIZED
throw new ApiException(StatusCode.UNAUTHORIZED, "Authentication is required");
```

**핸들러 우선순위** (`global/exception/advice/`):

| 클래스 | Order | 처리 대상 |
|--------|-------|---------|
| `ApiExceptionHandler` | `MIN_VALUE` | `ApiException`, `MethodArgumentNotValidException` |
| `AuthExceptionHandler` | `MIN_VALUE + 1` | `AccessDeniedException` |
| `GlobalExceptionHandler` | `MAX_VALUE` | `Exception` (폴백) |

**검증 오류 응답**: `MethodArgumentNotValidException`은 필드별 오류 Map을 body에 담아 반환한다.

---

## JPA 엔티티 패턴

### BaseEntity 상속

Audit 대상인 엔티티는 `db/entity/id/BaseEntity.java`를 상속한다.

```java
@Getter @Setter
@Entity
@Table(name = "table_name")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class SomeEntity extends BaseEntity { ... }
```

**BaseEntity 공통 필드**: `id` (IDENTITY), `createdAt`, `updatedAt`, `createdBy`, `lastModifiedBy`
- `@EntityListeners(AuditingEntityListener.class)` 적용 — 현재 로그인 사용자 ID 자동 기록

### 관계 매핑 규칙

- 기본 페칭 전략: `LAZY`
- N+1 해결: `@EntityGraph` 또는 `JOIN FETCH` 쿼리 사용
- 복합 PK 관계 엔티티: `@IdClass` 사용 (`AppUserRoleEntity` 참고)
- `@Embedded` 값 객체: `AddressEmbed` 참고

### 순환 참조 주의

`EmployeeEntity`는 supervisor(자기 참조)와 subordinates를 가진다. Converter에서 응답 변환 시 이 필드는 포함하지 않는다.

---

## 리포지토리 패턴

```java
// 기본 — JpaRepository만 상속
public interface SomeRepo extends JpaRepository<SomeEntity, Long> { }

// EntityGraph — N+1 방지
@EntityGraph(attributePaths = {"relation", "relation.nested"})
Optional<SomeEntity> findWithRelationFirstByUsername(String username);

// JPQL 수정 쿼리
@Modifying
@Query("UPDATE SomeEntity e SET e.count = e.count + 1 WHERE e.id = :id")
void incrementCount(@Param("id") Long id);

// 복잡한 JOIN FETCH + Projection
@Query("""
    SELECT u as appUser, e as employee
    FROM AppUserEntity u
    LEFT JOIN FETCH EmployeeEntity e ON e.appUser.id = u.id
    WHERE u.id IN :ids
    """)
List<UserEmployeeProjection> findFullByIdIn(@Param("ids") List<Long> ids);
```

**LIKE 검색 패턴**: Business/Service에서 `"%%%s%%".formatted(keyword.trim())`로 패턴을 생성해서 전달한다.

**Projection**: JOIN 결과 매핑용 인터페이스는 `db/projection/<Name>Projection.java`에 독립 파일로 정의한다. Repository 인터페이스 내부에 nested interface로 정의하지 않는다.

**메서드 시그니처 줄바꿈**: 파라미터가 여러 개여서 한 줄에 들어가지 않으면 각 파라미터를 줄바꿈하고 닫는 괄호도 별도 줄에 작성한다.

```java
Page<SomeEntity> findByFilter(
    @Param("kw") String kw,
    @Param("typeId") Long typeId,
    Pageable pageable
);
```

---

## DTO 패턴

### 요청 모델 (Req)

`@Data @AllArgsConstructor` 클래스 사용. 검증 어노테이션과 검증 그룹을 포함한다.

```java
@Data
@AllArgsConstructor
public class TeamCreateReq {

    @NotBlank(message = NAME_NOT_BLANK_MSG, groups = {CreateCheck.class})
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_LENGTH_MSG, groups = {CreateCheck.class})
    private String name;
}
```

**검증 그룹**: 하나의 Req 클래스를 여러 용도로 재사용할 때 내부 인터페이스로 그룹을 정의한다. (`SignupReq` 참고: `CreateCheck`, `ProfileUpdate`, `PasswordUpdate`, `AdminUpdate`)

컨트롤러에서는 `@Validated(SignupReq.ProfileUpdate.class)` 처럼 그룹을 지정한다.

### 응답 모델 (Res)

`@Data @Builder` 클래스 사용. 검증 어노테이션 없음.

```java
@Data
@Builder
public class TeamRes {
    private Long id;
    private String name;
}
```

### 다형성 요청 모델

```java
// 여러 값을 리스트로 받을 때
// { "list": ["ADMIN", "USER"] }
ListBodyReq<String> request
```

---

## Converter 패턴

```java
@Converter
public class TeamConverter {

    public TeamRes toResponse(TeamEntity entity) {
        return TeamRes.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    public TeamEntity toEntity(TeamCreateReq request) {
        return TeamEntity.builder()
            .name(request.getName())
            .build();
    }
}
```

- 메서드명: `toResponse()` (Entity → Res), `toEntity()` (Req → Entity)
- null 안전 처리: `Optional.ofNullable()` 사용
- 관계 엔티티 설정(연관관계 편의 메서드)은 Service에서 담당
- 단순 getter 위임은 람다 대신 메서드 참조 사용: `.map(e -> e.getName())` 대신 `.map(SomeEntity::getName)`

---

## Validation 상수 패턴

```java
@UtilityClass
public class TeamModelConst {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;

    public static final String NAME_NOT_BLANK_MSG = "Team name must not be blank";
    public static final String NAME_LENGTH_MSG =
        "Team name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters long";
}
```

- `@UtilityClass`로 인스턴스 생성 방지
- Req 클래스에서 `static import`로 사용

---

## 국제화 (i18n)

메시지 번들: `messages.properties`/`messages_ko.properties`(일반 메시지), `errors.properties`/`errors_ko.properties`(에러 메시지). `spring.messages.basename: messages,errors`로 등록한다.

### MessageUtil

`global/message/MessageUtil.java` — `MessageSource` + `LocaleContextHolder.getLocale()`을 감싼 `@Component`. `@RequiredArgsConstructor` + `private final MessageUtil messageUtil;`로 주입받아 사용한다.

```java
throw new ApiException(BAD_REQUEST, messageUtil.getMessage(PASSWORD_MISMATCH_ERR_MSG));
```

### *ModelConst vs *ErrorConst

검증 상수(`*ModelConst`)와 에러 메시지 키 상수(`*ErrorConst`)는 역할이 다르므로 분리한다.

| 클래스 | 용도 | 값 형식 | 사용처 |
|--------|------|---------|--------|
| `*ModelConst` | Bean Validation 어노테이션 `message` 속성 | `"{key}"` (Hibernate Validator `MessageInterpolator`가 해석) | `@NotBlank(message = NAME_NOT_BLANK_MSG)` |
| `*ErrorConst` | `messageUtil.getMessage()` 전달용 메시지 키 | 평문 키 문자열 (`"error.user.notFound"`, `{}` 없음) | `messageUtil.getMessage(NOT_FOUND_ERR_MSG)` |

**`*ErrorConst` 작성 규칙:**
- 도메인별로 `domain/<domain>/validation/<Entity>ErrorConst.java`에 위치 (`*ModelConst`와 동일한 패키지)
- 특정 도메인에 속하지 않는 전역 핸들러 메시지(`error.validation.failed` 등)는 `global/exception/GlobalErrorConst`에 위치
- 상수명은 `..._ERR_MSG` 접미사, `@UtilityClass` + `public static final String`으로 선언
- **메시지 키 문자열을 `messageUtil.getMessage("error.xxx")`처럼 직접 하드코딩하지 않는다** — 반드시 `*ErrorConst` 상수를 `static import`해서 사용한다

```java
@UtilityClass
public class AppUserErrorConst {
    public static final String NOT_FOUND_ERR_MSG = "error.user.notFound";
    public static final String PASSWORD_MISMATCH_ERR_MSG = "error.user.passwordMismatch";
}
```

### 테스트 환경 주의사항

- `src/test/resources/application.yaml`은 `src/main/resources/application.yaml`을 완전히 덮어쓴다(병합 아님). `spring.messages.basename: messages,errors` 등 메시지 설정을 테스트 프로파일에도 동일하게 작성해야 한다. 누락 시 `NoSuchMessageException: No message found under code 'error.xxx' for locale 'ko_KR'`가 발생한다.
- `@WebMvcTest` 슬라이스는 `@RestControllerAdvice` 핸들러가 의존하는 `MessageUtil`을 자동으로 주입하지 않는다. `@MockitoBean private MessageUtil messageUtil;`을 추가해 모킹한다.

---

## 보안 패턴

### 접근 제어

```java
// 인증 필요
@PreAuthorize("isAuthenticated()")

// 역할 기반
@PreAuthorize("hasAnyAuthority('ADMIN')")
```

### 현재 사용자 주입

```java
public ResponseEntity<Api<UserInfoRes>> getUser(
    @AuthenticationPrincipal UserSession userSession,
    @PathVariable Long userId
) { ... }
```

### 권한 검증 (사용자 자신의 리소스)

```java
// Service에서 세션 사용자와 요청 userId 비교
userService.validateUserId(userSession, userId);
```

### 역할 상수

`global/constants/RoleConst.java`: `SUPERADMIN`, `ADMIN`, `MANAGER`, `USER`

### API 경로 상수

`global/constants/RouteConst.java`:
- `API_V1` = `/api/v1`
- `PUBLIC_API_V1` = `/api/public/v1`

---

## 페이징 패턴

```java
// Business/Controller
var pageable = PageRequest.of(page, size, Sort.by("id"));
var result = service.getSomething(keyword, pageable);
return result.map(converter::toResponse);

// Repository
Page<SomeEntity> findBySomeFieldLike(String keyword, Pageable pageable);
```

---

## AOP

`global/aop/TimeTraceAop.java`: Business/Service 레이어의 메서드 실행 시간을 자동으로 `INFO` 레벨로 로깅한다. `@Converter`는 제외.

---

## 테스트 패턴

### 통합 테스트 (Repository/Service)

```java
@SpringBootTest
@AutoConfigureMockMvc
class TeamServiceTest {
    @Autowired private TeamService service;

    @Test
    @Transactional
    void createTeam() { ... }
}
```

### 컨트롤러 단위 테스트

```java
@WebMvcTest(AuthPublicApiController.class)
class AuthPublicApiControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private SomeDependency dependency;

    @Test
    @WithMockUser  // 인증된 사용자 시뮬레이션
    void checkSession() throws Exception {
        mockMvc.perform(get("/api/v1/auth/check-session"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.code").value(StatusCode.VALIDATED.getStatusCode()));
    }
}
```

### 테스트 상수

- `test/.../TestConst.java`: `TEST`, `SYSTEM` 등 공통 문자열
- `test/.../ApiTestConst.java`: `$.result.code`, `$.result.message`, `$.body` 등 jsonPath 상수
- 테스트 데이터: `src/test/resources/data-h2.sql`
- 테스트 DB 설정: `src/test/resources/application-dev.yaml` (H2)
