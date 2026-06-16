# QA 리포트 — i18n 백엔드 구현

검토 범위: 백엔드 전용 (프론트엔드 없음). `northwind-qa-boundary-check` 체크리스트 및 `01_plan.md`의 "QA 중점 검토 항목" 6개 집중 점검.

## Critical 수정 사항

없음. 빌드/런타임 오류 가능성이 있는 위반은 발견되지 않았다.

## Major 수정 사항

없음. 레이어 책임/예외 처리/i18n 컨벤션 위반은 발견되지 않았다.

## Minor 수정 사항

없음.

## QA 중점 검토 항목 점검 결과

1. **`UserSession.preferredLang == null` 시 `UserLocaleResolver` NPE 폴백** — PASS
   `resolveLocale`은 `Optional` 체인으로 구성되어 있고, `.map(UserSession::getPreferredLang)`가 null을 반환하면 `Optional.empty()`가 되어 `.filter(lang -> !lang.isBlank())`가 호출되지 않는다. 비인증/null/공백 모두 `acceptHeaderLocaleResolver.resolveLocale(request)`로 폴백 → NPE 없음.

2. **EntityGraph 변경이 기존 인증 흐름에 영향 없는지** — PASS
   `findWithRoleFirstByUsername`/`findFullFirstById`에 `"preferredLang"`가 가산적으로 추가되었고 기존 attributePaths는 유지된다. `AuthService.loadUserByUsername`은 LAZY 연관을 안전하게 `Optional.ofNullable(...).map(...).orElse("en")`로 읽는다. 로그인·프로필 흐름 테스트 통과.

3. **`createUser` 트랜잭션 롤백 경계 내 언어 할당 순서** — PASS
   `UserBusiness.createUser`(`@Transactional(rollbackFor = Exception.class)`) 내부에서 `assignRoleToUser` → `langService.getLangOrDefault` → `userService.updateLang` 순으로 실행되어 모두 동일 트랜잭션 경계 안에 포함된다.

4. **`preferredLangCode == null` 회원가입 'en' 폴백** — PASS
   `getLangOrDefault`가 null/공백을 `getDefaultLang()`('en')으로 처리. 테스트 `createUser_fallsBackToDefaultLangWhenNull`/`...WhenUnknown` 통과.

5. **`data-h2.sql`의 `supported_lang` INSERT 선행 실행** — PASS
   `INSERT INTO supported_lang (lang) VALUES ('en'), ('ko')`가 파일 맨 앞(1~3행)에 위치. `app_user.preferred_lang_id`는 nullable이며 시드에서 설정하지 않으므로 FK 위반 없음.

6. **`*ModelConst` `{key}` 형식 / `*ErrorConst` 평문 키** — PASS
   - `AppUserModelConst.LANG_ID_NULL_ERR_MSG = "{validation.appUser.preferredLangId.notNull}"` (중괄호 포함, `@NotNull(message = ...)`에 사용).
   - `LangErrorConst.NOT_FOUND_ERR_MSG = "error.lang.notFound"` (평문 키, `messageUtil.getMessage(...)`에 사용).

## 경계면/컨벤션 추가 점검

- **레이어 책임**: Lang 도메인은 단순 조회로 Business 없이 Controller → Service 직접 호출(team 패턴 일치). User 도메인의 다중 서비스 조합·트랜잭션은 Business에 위치. Converter는 변환만 담당. 혼재 없음.
- **응답 래퍼**: 모든 신규 엔드포인트가 `ResponseEntity<Api<T>>` 반환.
- **예외**: `ApiException` + `StatusCode`만 사용(`LangService` NOT_FOUND).
- **DI**: 전 클래스 `@RequiredArgsConstructor` + `private final`.
- **메시지 번들**: EN/KO `messages*`/`errors*` 양쪽에 `validation.appUser.preferredLangId.notNull`, `error.lang.notFound` 모두 존재. 테스트 프로파일 `application.yaml`에 `spring.messages.basename: messages,errors` 설정 확인.
- **`@WebMvcTest` MessageUtil 모킹**: `LangApiControllerTest`에 `@MockitoBean private MessageUtil messageUtil;` 존재.
- **응답 타입 일관성**: `SessionInfoRes`/`UserInfoRes`에 `preferredLang` 추가, `UserConverter`의 두 `toResponse`가 각각 세션 기준('en' 폴백)·DB 기준(null 폴백)으로 매핑하여 계약(`02_backend_contract.md`)과 일치.
- **신규 기능 테스트**: `LangServiceTest`, `LangApiControllerTest`, `UserBusinessLangTest` 모두 존재·통과.

## 테스트 결과

- 백엔드: PASS (조건부)
  - `.\gradlew.bat test` 결과 **196개 중 181개 통과 / 15개 실패**.
  - 실패한 15개는 전부 `sampledata.*CreateTest`(`CompanyCreateTest`, `CompanyTypeCreateTest`, `LangCreateTest`, `Order*CreateTest`, `Product*CreateTest`, `PurchaseOrder*CreateTest`, `StockTakeCreateTest`, `TaxStatusCreateTest`, `TeamCreateTest`, `TitleCreateTest`, `UserCreateTest`)로, `java.net.ConnectException`(실제 MySQL `dev` DB 연결 필요) 환경 의존 실패이며 본 i18n 변경과 무관하다(작업 지시상 무시 대상).
  - i18n 관련 신규/기존 테스트는 전부 통과.
- 프론트엔드: N/A (백엔드 전용 작업, 프론트엔드 변경 없음).

## 최종 판정

**PASS** — Critical/Major/Minor 위반 없음. 모든 QA 중점 항목 충족. 실패한 15개 테스트는 MySQL 환경 의존(`*CreateTest`)으로 변경 범위 밖이다. 별도 수정 없이 구현이 컨벤션을 준수한다.
