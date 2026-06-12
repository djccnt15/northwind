# 백엔드 API 계약 (S-40/41/42 주문 관리)

브랜치: `feature/order-management` / 루트: `C:/projects/northwind/.worktree/feature/order-management`

모든 응답은 공통 래퍼 `Api<T>`로 감싸진다.

```json
{ "serverTime": 1234567890, "result": { "code": 200, "message": "Success", "description": "OK" }, "body": <T> }
```

- 성공 200 / 생성 201 / NOT_FOUND 404 / 검증오류 1400(`body`에 `{field: message}` Map)
- 모든 엔드포인트 권한: `@PreAuthorize("hasAnyAuthority('ADMIN', 'ORDER')")` (컨트롤러 클래스 레벨)
- 베이스 경로: `/api/v1`

## 생성/수정된 파일

### 신규 (domain/order)
- `domain/order/controller/OrderApiController.java`
- `domain/order/business/OrderBusiness.java`
- `domain/order/service/OrderService.java`
- `domain/order/service/OrderDetailService.java`
- `domain/order/service/OrderStatusService.java`
- `domain/order/service/OrderDetailStatusService.java`
- `domain/order/converter/OrderConverter.java`
- `domain/order/converter/OrderDetailConverter.java`
- `domain/order/converter/OrderStatusConverter.java`
- `domain/order/converter/OrderDetailStatusConverter.java`
- `domain/order/converter/CompanyOptionConverter.java`
- `domain/order/converter/ProductOptionConverter.java`
- `domain/order/model/` : `OrderCreateReq`, `OrderDetailCreateReq`, `OrderStatusUpdateReq`, `OrderDetailStatusUpdateReq`,
  `OrderRes`, `OrderListRes`, `OrderDetailRes`, `OrderStatusRes`, `OrderDetailStatusRes`,
  `CompanyOptionRes`, `ProductOptionRes`, `CompanyRef`, `TaxStatusRef`, `ProductRef`, `OrderStatusRef`, `OrderDetailStatusRef`
- `domain/order/validation/` : `OrderModelConst`, `OrderErrorConst`, `OrderDetailModelConst`, `OrderDetailErrorConst` (신규)
  - 기존 `OrderStatusModelConst`/`OrderDetailStatusModelConst` 유지

### 보강 (db/repository)
- `OrdersRepo` : `findByFilter`, `findWithDetailById`, `findTotalAmountByIdIn`(+`OrderTotalProjection`) 추가 (`findByCustomerIdOrderByOrderDateDesc` 유지)
- `OrderDetailRepo` : `findWithRelationById`
- `OrderStatusRepo` : `findAllByOrderByIdAsc`, `findFirstByOrderByIdAsc`, `findByCode`
- `OrderDetailStatusRepo` : `findAllByOrderByIdAsc`, `findFirstByOrderByIdAsc`

### 기타
- `global/schedule/DataLoader.java` : 개발 MySQL DB용 `order_status`/`order_detail_status` 멱등 시드 추가 (count==0일 때만)
- `src/test/resources/data-h2.sql` : `tax_status`, `company_type`, `company`(2), `order_status`(5), `order_detail_status`(3), `orders`(1), `order_detail`(1) 시드 추가
- i18n: `messages*.properties`(order/orderDetail validation), `errors*.properties`(order/orderDetail 에러) 키 추가

### 테스트 (신규)
- `domain/order/service/OrderServiceTest.java`
- `domain/order/business/OrderBusinessTest.java`
- `domain/order/controller/OrderApiControllerTest.java`
- 전체 134개 테스트 통과 확인

> 주의: Service/Business 통합 테스트는 기존 테스트와 **동일한 컨텍스트 캐시**를 공유해야 H2(`DB_CLOSE_DELAY=-1`) 중복 시드 충돌을 피한다. 따라서 `@SpringBootTest @AutoConfigureMockMvc` 조합을 사용했다.

## API 엔드포인트

| 메서드 | 경로 | 요청 바디 | 응답 바디 |
|--------|------|----------|----------|
| GET | `/api/v1/order-statuses` | - | `OrderStatusRes[]` |
| GET | `/api/v1/order-detail-statuses` | - | `OrderDetailStatusRes[]` |
| GET | `/api/v1/orders/company-types` | - | `CompanyTypeRes[]` |
| GET | `/api/v1/orders/companies?type={typeId}&keyword=` | - | `CompanyOptionRes[]` |
| GET | `/api/v1/orders/products?keyword=` | - | `ProductOptionRes[]` (discontinued=false만) |
| GET | `/api/v1/orders/tax-statuses` | - | `TaxStatusRes[]` |
| GET | `/api/v1/orders?page=&size=&status=&dateFrom=&dateTo=&keyword=` | - | `Page<OrderListRes>` |
| GET | `/api/v1/orders/{id}` | - | `OrderRes` |
| POST | `/api/v1/orders` | `OrderCreateReq` | `OrderRes` (201) |
| PATCH | `/api/v1/orders/{id}/status` | `OrderStatusUpdateReq` | `OrderRes` |
| PATCH | `/api/v1/orders/{id}/details/{detailId}/status` | `OrderDetailStatusUpdateReq` | `OrderRes` |

### 쿼리 파라미터 상세
- `/orders` 목록: `page`(기본 0), `size`(기본 20), `status`(orderStatus id, 선택), `dateFrom`/`dateTo`(`yyyy-MM-dd`, `orderDate` 기준, 선택), `keyword`(고객사명 LIKE, 선택). 정렬: `orderDate DESC`.
- `/orders/companies`: `type`(company_type id, 선택 — 고객사/배송사 구분에 사용), `keyword`(회사명 LIKE, 선택). 최대 100건.
- `/orders/products`: `keyword`(상품명/코드 LIKE, 선택). 최대 100건, discontinued=false만.

## 요청 DTO 정의

### OrderCreateReq (POST /orders)
| 필드 | 타입 | 검증 | 비고 |
|------|------|------|------|
| customerId | number(Long) | NotNull | 고객사 id |
| shipperId | number(Long)\|null | - | 배송사 id (선택) |
| requiredDate | string(`yyyy-MM-dd`)\|null | - | 요청 배송일 (엔티티 `required_date` 컬럼) |
| taxStatusId | number(Long) | NotNull | 세금유형 id |
| paymentType | string\|null | - | 결제수단 |
| shippingFee | number(int)\|null | - | 배송비 |
| notes | string\|null | - | 메모 |
| orderDetails | OrderDetailCreateReq[] | NotEmpty, 각 항목 @Valid | 주문 항목 |

### OrderDetailCreateReq
| 필드 | 타입 | 검증 |
|------|------|------|
| productId | number(Long) | NotNull |
| quantity | number(int) | NotNull, Min 1 |
| discount | number(int)\|null | 0~100 (null이면 0으로 저장) |

### OrderStatusUpdateReq / OrderDetailStatusUpdateReq
| 필드 | 타입 | 검증 |
|------|------|------|
| statusId | number(Long) | NotNull |

## 응답 DTO 정의 (프론트 entities/order.ts 매핑용)

### OrderStatusRes
| 필드 | 타입 |
|------|------|
| id | number |
| code | string (`PENDING`/`PAID`/`SHIPPED`/`DELIVERED`/`CANCELLED`) |
| name | string (접수/결제완료/출고/배송완료/취소) |
| sortOrder | string\|null (현재 enum `ASC` 문자열, 의미 약함 — id 순서로 정렬됨) |

### OrderDetailStatusRes
| 필드 | 타입 |
|------|------|
| id | number |
| name | string (대기/출고/취소) |
| sortOrder | string\|null |

### CompanyOptionRes
| 필드 | 타입 |
| id | number |
| name | string |

### ProductOptionRes
| 필드 | 타입 |
| id | number |
| name | string |
| unitPrice | number (BigDecimal 직렬화) |

### CompanyTypeRes (재사용, domain/company)
`{ id: number, companyType: string }`

### TaxStatusRes (재사용, domain/tax)
`{ id: number, status: string }`

### OrderListRes (GET /orders 목록 item)
| 필드 | 타입 |
|------|------|
| id | number |
| orderDate | string(`yyyy-MM-dd`) |
| customerName | string |
| shipperName | string\|null |
| status | { id:number, code:string, name:string } (OrderStatusRef) |
| totalAmount | number (subtotal 합 + shippingFee) |

> `Page<OrderListRes>` 형태: `body.content`(item 배열), `body.totalElements`, `body.totalPages`, `body.number`, `body.size` 등 Spring Page 표준 필드. (company 목록과 동일 구조)

### OrderDetailRes (OrderRes.orderDetails item)
| 필드 | 타입 |
|------|------|
| id | number |
| product | { id:number, name:string } (ProductRef) |
| unitPrice | number (생성 시점 스냅샷) |
| quantity | number |
| discount | number |
| subtotal | number = `unitPrice * quantity * (1 - discount/100)`, 소수 2자리 HALF_UP |
| status | { id:number, name:string } (OrderDetailStatusRef) |

### OrderRes (GET /orders/{id}, POST /orders, PATCH 응답)
| 필드 | 타입 |
|------|------|
| id | number |
| orderDate | string(`yyyy-MM-dd`) |
| requiredDate | string(`yyyy-MM-dd`)\|null |
| shippedDate | string(`yyyy-MM-dd`)\|null |
| paidDate | string(`yyyy-MM-dd`)\|null |
| shippingFee | number\|null |
| taxRate | number\|null |
| paymentType | string\|null |
| notes | string\|null |
| customer | { id:number, name:string } (CompanyRef) |
| shipper | { id:number, name:string }\|null |
| taxStatus | { id:number, status:string } (TaxStatusRef)\|null |
| status | { id:number, code:string, name:string } (OrderStatusRef) |
| orderDetails | OrderDetailRes[] (id 오름차순) |
| totalAmount | number = `sum(subtotal) + shippingFee` |

## 비즈니스 규칙 (구현됨)

- **주문 생성**: `orderDate`=오늘, `appUser`=로그인 사용자(세션 id, `getReferenceById`), `orderStatus`=PENDING(첫 상태), 각 `orderDetail.status`=대기(첫 상태). `unitPrice`/`standardUnitCost`는 생성 시점 상품 가격 스냅샷.
- **헤더 상태 전이** (`OrderService.validateStatusTransition`, code 기준):
  - 동일 상태로 변경 불가
  - 종료 상태(`DELIVERED`, `CANCELLED`)에서는 더 이상 전이 불가
  - `CANCELLED`는 비종료 상태 어디서든 가능
  - 그 외는 rank(PENDING=1<PAID=2<SHIPPED=3<DELIVERED=4) 기준 **전진만** 허용 (역행 차단). 위반 시 `ApiException(BAD_REQUEST, error.order.invalidStatusTransition)`
  - `PAID`로 전이 시 `paidDate`=오늘, `SHIPPED`로 전이 시 `shippedDate`=오늘 자동 기록
- **항목 상태 변경**: `order_detail_status` 독립 변경, 헤더 상태 무관. `detailId`가 `orderId`에 속하지 않으면 404.
- **totalAmount/subtotal**: BigDecimal, subtotal은 소수 2자리 HALF_UP. 목록(`OrderListRes`)의 totalAmount는 N+1 방지를 위해 `findTotalAmountByIdIn` 집계 쿼리로 page id 일괄 계산(컬렉션 fetch + 페이지네이션 동시 사용 회피).

## 프론트엔드 참고 사항

1. **목록 totalAmount**는 서버가 계산해 내려준다. 생성 폼(S-42)의 클라이언트 미리보기는 동일 공식 `sum(unitPrice*qty*(1-discount/100)) + shippingFee`, **소수 2자리 반올림(HALF_UP)**을 사용해야 백엔드와 일치한다.
2. **requiredDate** 필드명에 주의 (엔티티 컬럼은 `required_date`, DTO/JSON 키는 `requiredDate`).
3. **taxRate**는 현재 생성 시 항상 null로 저장된다(세율 계산 미구현). 프론트는 표시용으로만 사용, null 허용.
4. lookup 엔드포인트(`/orders/company-types`, `/orders/companies`, `/orders/products`, `/orders/tax-statuses`)는 모두 `ADMIN,ORDER` 권한으로 호출 가능(권한 경계 회피용 재노출).
5. 상태 전이 `<select>`는 백엔드 규칙(전진/취소만)에 맞춰 옵션을 제한하면 1400/400을 피할 수 있다. 잘못된 전이는 백엔드가 400으로 차단한다.

## QA 중점(참고)
- 권한 경계: lookup 엔드포인트가 `ADMIN,ORDER`로 동작, 기존 Company/Product 컨트롤러 권한 불변.
- 경계면 타입: 위 응답 DTO 표와 `frontend/src/entities/order.ts` 1:1 대조 필요.
- 상태 전이/금액 계산 일치 여부는 위 규칙 기준.
