# QA 리포트

대상 브랜치: `feature/backend-i18n-validation-error`
검토 범위: 백엔드 전용 (DTO 검증/에러 메시지 i18n 적용 + `*ErrorConst` 상수 추출)
검토 기반: `_workspace/02_backend_contract.md`

## 검토 결과: PASS

## 확인 사항

### 1. 컨벤션 준수
- 신규 `*ErrorConst` 클래스가 `*ModelConst`와 동일한 `domain/<domain>/validation/` 패키지에 위치 (도메인 비종속 핸들러 메시지는 `global/exception/GlobalErrorConst`로 분리)
- `@UtilityClass` + `public static final String ..._ERR_MSG` 명명 규칙 준수, 값은 평문 메시지 키(`{}` 미사용)로 `*ModelConst`의 `{key}` placeholder 형식과 명확히 구분됨
- `MessageUtil`은 `@Component` + `@RequiredArgsConstructor` 표준 DI 패턴 준수
- `domain/auth/validation` 신규 패키지 생성은 `AuthService`/`AuthPublicApiController`가 `auth` 도메인에 속한다는 점에서 합리적인 위치 선정

### 2. 하드코딩 제거 검증
- `grep -rn 'messageUtil\.getMessage\("error\.' src/main/java` → **매치 없음** (호출부에 직접 작성된 `"error.xxx"` 리터럴 49건 모두 `*ErrorConst` 상수의 `static import`로 전환 완료)

### 3. 테스트 환경 설정
- `src/test/resources/application.yaml`이 `src/main/resources/application.yaml`을 완전히 오버라이드하는 구조이므로, `spring.messages.basename: messages,errors`를 누락 없이 동일하게 추가했는지 확인 — 정상 추가됨
- `@WebMvcTest` 슬라이스 컨텍스트가 `@RestControllerAdvice`의 신규 `MessageUtil` 의존성을 해결하지 못하는 문제를, 해당되는 5개 테스트 클래스 모두에 `@MockitoBean`으로 보강했는지 확인 — 모두 반영됨. 테스트가 에러 메시지의 정확한 문자열을 assert하지 않으므로 모킹된(`null`) 반환값이어도 안전함을 grep으로 재확인

### 4. 빌드/테스트 실행
- `./gradlew.bat test -x buildFrontend --console=plain` → `BUILD SUCCESSFUL in 1m 26s`, 전체 테스트 통과 (실패 없음)

## 잔존 이슈
없음
</content>
