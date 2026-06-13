# 문서 반영 결과

QA 최종 판정: **PASS** (`04_qa_report.md`) → 문서 갱신 진행.

## 변경된 문서

### doc/StoryBoard.md
- 1절 전체 화면 목록 표: S-50/51/52 상태 `🔲` → `✅`
- 3절 상세 스토리보드 헤더: `### S-50 발주 목록`, `### S-51 발주 상세`, `### S-52 발주 생성` 의 `🔲` → `✅`
- 6절 미구현 화면 개발 우선순위 표: 완료된 "S-50/51/52 발주 관리" 행 제거, 남은 항목 우선순위 1부터 재정렬 (S-10 홈 대시보드 → 1, S-63 재고 실사 → 2). 표 위 제외 안내 문구에 "S-50/51/52(발주)" 추가.

**코드 검증 (✅ 표기 근거):**
- `frontend/src/app/router.tsx` (L81-93): `PurchaseRoute` 가드 하위에 `/purchase-orders`, `/purchase-orders/new`, `/purchase-orders/:id` 3개 라우트 등록 확인 (order 그룹과 동일하게 `/new`가 `/:id`보다 먼저 매칭).
- `src/main/java/com/djccnt15/northwind/domain/purchase/controller/PurchaseOrderApiController.java` 존재 확인. `02_backend_contract.md`의 엔드포인트(`GET /purchase-orders`, `GET /purchase-orders/{id}`, `POST /purchase-orders`, `PATCH /purchase-orders/{id}/status` 등) 구현 완료(백엔드 테스트 전체 통과).
- 권한 3자 일치(컨트롤러 `@PreAuthorize("hasAnyAuthority('ADMIN', 'PURCHASE')")` / 프론트 `PurchaseRoute` / navbar) — QA 리포트 4번 항목에서 확인됨.

### doc/EDR.md
- mermaid ERD `purchase_order_status` 엔티티에 `varchar code UK` 컬럼 추가.
- mermaid ERD에 신규 엔티티 `purchase_order_detail` 블록 추가:
  `id PK`, `int quantity`, `decimal unit_price`, `bigint product_id FK`, `bigint purchase_order_id FK` + audit 컬럼(`created_at`/`created_by`/`last_modified_by`/`updated_at`).
- 관계 섹션에 2개 관계 추가:
  `purchase_orders ||--o{ purchase_order_detail : "contains"`,
  `product ||--o{ purchase_order_detail : "ordered"` (order_detail 관계 패턴 미러링).

**코드 검증 (엔티티 실제 필드 대조):**
- `db/entity/PurchaseOrderStatusEntity.java` (L27-29): `@Column(... unique = true) private String code;` — `code` UK 컬럼 추가 확인.
- `db/entity/PurchaseOrderDetailEntity.java`: `Integer quantity`, `BigDecimal unitPrice`, `@ManyToOne ProductEntity product`(product_id FK), `@ManyToOne PurchaseOrderEntity purchaseOrder`(purchase_order_id FK). `BaseEntity` 상속으로 audit 컬럼 포함. order_detail과 달리 `discount`/`standard_unit_cost`/품목상태 없음 — 02_backend_contract.md 및 계획과 일치하므로 ERD에도 해당 컬럼 미포함.

## 변경 없음
- `doc/PRD.md`: 영향 없음. 발주 라인아이템은 PRD의 참조 무결성 원칙(order_detail과 동일) 연장이며 신규 비즈니스 규칙/스코프 변경이 아니다 (`01_plan.md` doc/ 영향 범위에서도 PRD는 영향 없음으로 명시).

## 보류
- 없음.
