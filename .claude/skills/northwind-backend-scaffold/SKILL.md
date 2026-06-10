---
name: northwind-backend-scaffold
description: >
  Northwind 백엔드(Spring Boot 3.5/Java 21)에 신규 도메인을 추가할 때 Entity → Repository → Converter →
  Service → [Business] → Controller → Validation(*ModelConst/*ErrorConst) → Test 순서로 스캐폴딩하기 위한
  레이어별 코드 템플릿과 체크리스트. "도메인 추가", "Entity/Repository/Service/Controller 구현",
  "백엔드 CRUD API 작성", "백엔드 레이어 구현" 등 신규 백엔드 레이어 작업에서 사용한다.
---

## 핵심 체크리스트

신규 도메인 `<Domain>` 추가 시 다음 순서로 파일을 생성한다 (`src/CLAUDE.md` 패키지 구조 기준):

1. `db/entity/<Domain>Entity.java` — 생성/수정 이력 추적이 필요하면 `BaseEntity` 상속
2. `db/repository/<Domain>Repo.java` — `JpaRepository`, 필요 시 `@EntityGraph`/`@Query`
3. `domain/<domain>/converter/<Domain>Converter.java` — `toResponse()`/`toEntity()`
4. `domain/<domain>/service/<Domain>Service.java` — 단일 Repo 접근, 도메인 규칙 검증
5. `domain/<domain>/business/<Domain>Business.java` — **복잡한 도메인만** (여러 Service 조합 + `@Transactional`). 단순 CRUD 도메인은 생략하고 Controller가 Service를 직접 호출
6. `domain/<domain>/controller/<Domain>ApiController.java` — `ResponseEntity<Api<T>>` 반환
7. `domain/<domain>/validation/<Domain>ModelConst.java` + `<Domain>ErrorConst.java` — 아래 "i18n 체크리스트" 참고
8. 테스트: Service/Repository는 `@SpringBootTest`, Controller는 `@WebMvcTest`

레이어별 코드 템플릿: `references/layer-templates.md` 참고.

## i18n 체크리스트 (자주 누락되는 항목)

- [ ] `*ModelConst`(Bean Validation `message` 속성용, `"{key}"` 형식)와 `*ErrorConst`(`messageUtil.getMessage()` 전달용 평문 키)를 **별도 클래스**로 분리했는가
- [ ] 메시지 키 문자열을 직접 하드코딩하지 않고 `*ErrorConst` 상수를 `static import`로 사용했는가
- [ ] `messages.properties`/`messages_ko.properties`(또는 `errors.*`)에 키를 등록했는가
- [ ] `src/test/resources/application*.yaml`에도 `spring.messages.basename: messages,errors`가 동일하게 설정되어 있는가 (테스트 프로파일은 main 설정을 완전히 덮어씀 — 누락 시 `NoSuchMessageException`)
- [ ] `@WebMvcTest` 컨트롤러 테스트에 `@MockitoBean private MessageUtil messageUtil;`을 추가했는가 (`@RestControllerAdvice`가 의존하므로 자동 주입 안 됨)

상세 패턴: `src/CLAUDE.md`의 "국제화(i18n)" 섹션 참고.
