# 문서 반영 결과

QA 최종 판정: **PASS** (`04_qa_report.md`) → 문서 동기화 진행.

## 변경된 문서

- **doc/StoryBoard.md** (`C:/projects/northwind/.worktree/feature/order-management/doc/StoryBoard.md`)
  - 1절 전체 화면 목록 표: S-40 주문 목록 / S-41 주문 상세 / S-42 주문 생성 상태 `🔲` → `✅`
  - 3절 상세 스토리보드 헤더: `### S-40 주문 목록`, `### S-41 주문 상세`, `### S-42 주문 생성` 의 `🔲` → `✅`
  - 6절 "미구현 화면 개발 우선순위" 표: S-40/41/42 주문 관리 행 제거, 남은 항목 우선순위 1부터 재정렬
    (S-10 홈 대시보드=1, S-50/51/52 발주=2, S-63 재고 실사=3). 표 상단 제외 주석에 "S-40/41/42(주문)" 추가.

## 코드 검증 (✅ 표기 근거)

worktree(`C:/projects/northwind/.worktree/feature/order-management`)의 실제 코드로 교차 검증:

- **프론트 라우터** `frontend/src/app/router.tsx`: `OrderRoute` 가드 블록에 `/orders`(Orders), `/orders/new`(OrderNew), `/orders/:id`(OrderDetail) 라우트 등록 확인. `/orders/new`가 `/orders/:id`보다 먼저 등록되어 라우트 우선순위 정확.
- **페이지 컴포넌트**: `frontend/src/pages/orders.tsx`, `order-detail.tsx`, `order-new.tsx` 3개 파일 존재 확인.
- **네비게이션** `frontend/src/widgets/navbar-left.tsx`: ADMIN 또는 ORDER 권한 시 노출되는 "Orders" `NavLink to="/orders"` 존재 확인.
- **백엔드 컨트롤러** `src/main/java/com/djccnt15/northwind/domain/order/controller/OrderApiController.java`:
  클래스 레벨 `@RequestMapping(API_V1)` + `@PreAuthorize("hasAnyAuthority('ADMIN', 'ORDER')")`, 02_backend_contract.md의 엔드포인트 11개 모두 구현 확인
  (`GET /order-statuses`, `GET /order-detail-statuses`, `GET /orders/company-types`, `GET /orders/companies`, `GET /orders/products`, `GET /orders/tax-statuses`, `GET /orders`, `GET /orders/{id}`, `POST /orders`, `PATCH /orders/{id}/status`, `PATCH /orders/{id}/details/{detailId}/status`).

`01_plan.md`의 "doc/ 영향 범위"(S-40/41/42 🔲→✅, 6절 표 갱신)와 실제 코드 상태가 일치함.

## 변경 없음

- **doc/PRD.md** / **doc/EDR.md**: 변경 없음.
  - 사유: 이번 작업은 기존 ERD 구조(orders/order_detail/order_status/order_detail_status/tax_status 등)를 그대로 사용했으며 신규 테이블/컬럼/관계 추가가 없음. PRD의 "명시적으로 만들지 않을 것" 범위 변경도 없음. `01_plan.md`에서도 "doc/PRD.md/doc/EDR.md: 영향 없음"으로 명시되어 코드 검증 결과와 일치.
- **doc/StoryBoard.md 2절 화면 흐름도**: 좌측 네비게이션 바 목록에 "주문 → S-40 주문 목록" 항목이 이미 존재하여 변경 없음(`01_plan.md` 기재와 일치).
