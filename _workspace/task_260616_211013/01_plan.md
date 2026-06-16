# 구현 계획

## 백엔드 작업 항목

### A. 메시지 키 추가 (src/main/resources/)

| 파일 | 추가 키 |
|------|---------|
| `errors.properties` | `error.lang.notFound=Language not found` |
| `errors_ko.properties` | `error.lang.notFound=지원하지 않는 언어입니다` |
| `messages.properties` | `validation.appUser.preferredLangId.notNull=Preferred language ID is required` |
| `messages_ko.properties` | `validation.appUser.preferredLangId.notNull=선호 언어는 필수입니다` |

### B. DB Repository 수정

**`db/repository/SupportedLangRepo.java`** — 메서드 추가:
```java
Optional<SupportedLangEntity> findFirstByLang(String lang);
```

**`db/repository/AppUserRepo.java`** — EntityGraph 수정:
```java
// findWithRoleFirstByUsername: "preferredLang" 추가
@EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole", "preferredLang"})

// findFullFirstById: "preferredLang" 추가
@EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole", "team", "preferredLang"})
```

### C. Lang 도메인 신규 (domain/lang/)

순서: Model → Converter → Validation → Service → Controller

1. **`domain/lang/model/LangRes.java`** (`@Data @Builder`)
   - `Long id`, `String lang`

2. **`domain/lang/converter/LangConverter.java`** (`@Converter`)
   - `toResponse(SupportedLangEntity)` → `LangRes`

3. **`domain/lang/validation/LangErrorConst.java`** (`@UtilityClass`)
   - `NOT_FOUND_ERR_MSG = "error.lang.notFound"`

4. **`domain/lang/service/LangService.java`** (`@Service`, `MessageUtil` 주입)
   - `getLangs()` → `List<SupportedLangEntity>` (Sort.by("lang"))
   - `getLang(Long id)` → `SupportedLangEntity` (없으면 ApiException NOT_FOUND)
   - `getLangOrDefault(String langCode)` → null/빈문자면 'en' 폴백, 없는 코드도 'en' 폴백

5. **`domain/lang/controller/LangApiController.java`** (`@RestController`, `API_V1 + "/lang"`)
   - `GET /api/v1/lang` — `@PreAuthorize("isAuthenticated()")`
   - Controller → Service 직접 호출 (Business 없음, team 도메인 패턴 참고)

### D. UserSession + AuthService 수정 (global/config/security/)

1. **`global/config/security/model/UserSession.java`**
   - `private String preferredLang;` 필드 추가

2. **`global/config/security/AuthService.java`** (`loadUserByUsername`)
   - `Optional.ofNullable(entity.getPreferredLang()).map(SupportedLangEntity::getLang).orElse("en")`
   - `UserSession.builder()...preferredLang(preferredLang).build()`

### E. User 도메인 수정 (domain/user/)

1. **`domain/user/model/SessionInfoRes.java`** — `private String preferredLang;` 추가
2. **`domain/user/model/UserInfoRes.java`** — `private String preferredLang;` 추가
3. **`domain/user/model/SignupReq.java`** — `private String preferredLangCode;` 추가 (검증 어노테이션 없음)
4. **`domain/user/validation/AppUserModelConst.java`** — `LANG_ID_NULL_ERR_MSG = "{validation.appUser.preferredLangId.notNull}"` 추가
5. **`domain/user/model/UpdateLangReq.java`** (신규, `@Data @AllArgsConstructor`)
   - `@NotNull(message = LANG_ID_NULL_ERR_MSG, groups = {UpdateLang.class}) Long preferredLangId`
   - 내부 인터페이스: `interface UpdateLang {}`
6. **`domain/user/converter/UserConverter.java`** 수정
   - `toResponse(UserSession)` → `.preferredLang(userSession.getPreferredLang())` 추가
   - `toResponse(AppUserEntity)` → `Optional.ofNullable(entity.getPreferredLang()).map(SupportedLangEntity::getLang).orElse(null)` + builder에 `.preferredLang()` 추가
7. **`domain/user/service/UserService.java`** — `updateLang(AppUserEntity, SupportedLangEntity)` 추가
8. **`domain/user/business/UserBusiness.java`** 수정
   - `private final LangService langService;` 의존성 추가
   - `createUser()`: `userRoleService.assignRoleToUser()` 이후 `langService.getLangOrDefault(request.getPreferredLangCode())` → `userService.updateLang()` 호출
   - `updateLang(UserSession, Long, UpdateLangReq)` 신규 메서드 추가 (`@Transactional`)
9. **`domain/user/controller/UserApiController.java`** — `PATCH {userId}/lang` 엔드포인트 추가

### F. 커스텀 LocaleResolver (global/config/)

1. **`global/config/UserLocaleResolver.java`** (LocaleResolver 구현)
   - `resolveLocale()`: SecurityContextHolder → UserSession.preferredLang → Locale.forLanguageTag()
   - 비인증/lang없음 → `new AcceptHeaderLocaleResolver().resolveLocale(request)` 폴백
   - `setLocale()`: 빈 구현 (언어 변경은 UserBusiness.updateLang이 담당)
2. **`global/config/WebConfig.java`** — `@Bean public LocaleResolver localeResolver()` 추가

### G. 테스트 데이터

**`src/test/resources/data-h2.sql`** — 맨 앞에 추가:
```sql
INSERT INTO supported_lang (lang) VALUES ('en'), ('ko');
```

### H. 기존 테스트 수정

- `AuthPublicApiControllerTest.java` — 회원가입 관련 테스트에서 `preferredLangCode` 추가 또는 null 허용 확인
- 기존 테스트가 `SessionInfoRes.preferredLang` 필드를 검증하는 경우 업데이트

## 참고 패턴

- **단순 조회 도메인 (Business 없음)**: `domain/team/controller/TeamApiController.java` + `domain/team/service/TeamService.java`
- **Business 있는 User 도메인**: `domain/user/business/UserBusiness.java` 기존 `updateProfile()` 패턴
- **에러 상수**: `domain/team/validation/TeamErrorConst.java` 참고
- **LocaleResolver 등록**: `global/config/WebConfig.java`의 `@Bean` 등록 방식 참고

## doc/ 영향 범위

- `doc/StoryBoard.md`:
  - **S-03 (회원가입)**: `preferredLangCode` 파라미터 추가 — 백엔드 API 수정
  - **S-11 (내 프로필)**: `GET /api/v1/lang` + `PATCH /api/v1/user/{userId}/lang` 신규 API 구현
  - StoryBoard의 S-11 "선호 언어 변경" 항목이 ✅로 갱신될 수 있음 (Phase 5에서 doc sub-agent가 최종 판단)
- `doc/PRD.md`: i18n 전략 문서 이미 갱신됨(76e75736) — 추가 변경 없음
- `doc/EDR.md`: DB 스키마 변경 없음 — 추가 변경 없음

## QA 중점 검토 항목

1. `UserSession.preferredLang`이 `null`인 경우 `UserLocaleResolver`가 NPE 없이 폴백하는지
2. `findWithRoleFirstByUsername` EntityGraph 변경이 기존 인증 흐름(로그인/check-session)에 영향 없는지
3. `UserBusiness.createUser()` 트랜잭션 내에서 `langService.getLangOrDefault()` → `userService.updateLang()` 순서가 롤백 범위 내에 있는지
4. 회원가입 시 `preferredLangCode=null`이어도 'en' 폴백으로 정상 동작하는지
5. `data-h2.sql`의 `supported_lang` INSERT가 다른 테이블의 외래키보다 먼저 실행되는지
6. `*ModelConst` 메시지 키가 `{key}` 형식(중괄호 포함)인지 — `*ErrorConst`는 중괄호 없는 평문 키
