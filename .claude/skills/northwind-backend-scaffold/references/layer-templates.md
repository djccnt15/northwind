# 백엔드 레이어 템플릿

`<Domain>` = PascalCase 도메인명 (예: `Order`), `<domain>` = camelCase/패키지명 (예: `order`)

## Entity (`db/entity/<Domain>Entity.java`)

```java
@Getter @Setter
@Entity
@Table(name = "<table_name>")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class <Domain>Entity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    // 관계: 기본 LAZY, N+1은 @EntityGraph 또는 JOIN FETCH로 해결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_id")
    private RelatedEntity related;
}
```

생성/수정 이력 추적이 불필요하면 `extends BaseEntity`를 생략한다.

## Repository (`db/repository/<Domain>Repo.java`)

```java
public interface <Domain>Repo extends JpaRepository<<Domain>Entity, Long> {

    // N+1 방지
    @EntityGraph(attributePaths = {"related"})
    Optional<<Domain>Entity> findWithRelatedById(Long id);

    // 페이징 검색 (LIKE)
    Page<<Domain>Entity> findByNameLike(String keyword, Pageable pageable);
}
```

## Converter (`domain/<domain>/converter/<Domain>Converter.java`)

```java
@Converter
public class <Domain>Converter {

    public <Domain>Res toResponse(<Domain>Entity entity) {
        return <Domain>Res.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    public <Domain>Entity toEntity(<Domain>CreateReq request) {
        return <Domain>Entity.builder()
            .name(request.getName())
            .build();
    }
}
```

## Service (`domain/<domain>/service/<Domain>Service.java`)

```java
@Service
@RequiredArgsConstructor
public class <Domain>Service {

    private final <Domain>Repo <domain>Repo;
    private final <Domain>Converter <domain>Converter;
    private final MessageUtil messageUtil;

    public Page<<Domain>Res> getList(String keyword, Pageable pageable) {
        var pattern = "%%%s%%".formatted(keyword.trim());
        return <domain>Repo.findByNameLike(pattern, pageable).map(<domain>Converter::toResponse);
    }

    public <Domain>Entity getEntityOrThrow(Long id) {
        return <domain>Repo.findById(id)
            .orElseThrow(() -> new ApiException(StatusCode.NOT_FOUND,
                messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }
}
```

## Business (`domain/<domain>/business/<Domain>Business.java`) — 복잡한 도메인만

여러 Service를 조합하거나 트랜잭션 경계가 필요할 때만 생성한다. 단순 CRUD 도메인(예: `team`/`title`)은 생략하고 Controller가 Service를 직접 호출한다.

```java
@Business
@RequiredArgsConstructor
public class <Domain>Business {

    private final <Domain>Service <domain>Service;
    private final OtherService otherService;
    private final <Domain>Converter <domain>Converter;

    @Transactional
    public <Domain>Res create(<Domain>CreateReq request) {
        var entity = <domain>Converter.toEntity(request);
        // 여러 Service 조합 + 연관관계 설정
        otherService.linkSomething(entity);
        return <domain>Converter.toResponse(<domain>Service.save(entity));
    }
}
```

## Controller (`domain/<domain>/controller/<Domain>ApiController.java`)

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1 + "/<domain>")
public class <Domain>ApiController {

    private final <Domain>Service <domain>Service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Api<PageRes<<Domain>Res>>> getList(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("id"));
        return ResponseEntity.ok(Api.OK(<domain>Service.getList(keyword, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Api<<Domain>Res>> create(
        @Validated(<Domain>CreateReq.CreateCheck.class) @RequestBody <Domain>CreateReq request
    ) {
        return ResponseEntity.ok(Api.CREATED(<domain>Business.create(request)));
    }
}
```

단순 도메인은 `<domain>Business.create(...)` 대신 `<domain>Service.create(...)`를 직접 호출한다.

## Validation 상수 — `*ModelConst` (`domain/<domain>/validation/<Domain>ModelConst.java`)

Bean Validation 어노테이션의 `message` 속성에 사용하는 `"{key}"` 형식 키를 정의한다.

```java
@UtilityClass
public class <Domain>ModelConst {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;

    public static final String NAME_NOT_BLANK_MSG = "{<domain>.name.notBlank}";
    public static final String NAME_LENGTH_MSG = "{<domain>.name.length}";
}
```

## 에러 메시지 키 — `*ErrorConst` (`domain/<domain>/validation/<Domain>ErrorConst.java`)

`messageUtil.getMessage()`에 전달하는 평문 키를 정의한다. `*ModelConst`와 절대 합치지 않는다.

```java
@UtilityClass
public class <Domain>ErrorConst {

    public static final String NOT_FOUND_ERR_MSG = "error.<domain>.notFound";
    public static final String NAME_DUPLICATE_ERR_MSG = "error.<domain>.nameDuplicate";
}
```

`messages.properties`/`messages_ko.properties`(또는 `errors.*`)에 `*ModelConst`/`*ErrorConst` 양쪽 키를 모두 등록한다.

## 테스트 — Service (`src/test/.../service/<Domain>ServiceTest.java`)

```java
@SpringBootTest
class <Domain>ServiceTest {

    @Autowired private <Domain>Service <domain>Service;

    @Test
    @Transactional
    void getList() {
        var result = <domain>Service.getList("", PageRequest.of(0, 10, Sort.by("id")));
        assertThat(result.getContent()).isNotEmpty();
    }
}
```

## 테스트 — Controller (`src/test/.../controller/<Domain>ApiControllerTest.java`)

```java
@WebMvcTest(<Domain>ApiController.class)
class <Domain>ApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private <Domain>Service <domain>Service;
    @MockitoBean private MessageUtil messageUtil; // @RestControllerAdvice 의존성 — 자동 주입 안 됨

    @Test
    @WithMockUser
    void getList() throws Exception {
        mockMvc.perform(get(API_V1 + "/<domain>"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(RESULT_CODE).value(StatusCode.OK.getStatusCode()));
    }
}
```
