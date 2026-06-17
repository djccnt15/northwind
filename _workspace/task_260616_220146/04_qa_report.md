# QA 리포트

## 최종 판정: PASS

프론트엔드 i18n(react-i18next) 적용물에 대해 경계면/컨벤션 검토를 수행했다.
Critical/Major 위반 없음. 빌드 통과. 수정 사항 없음(원본이 컨벤션을 준수).

## 검토 항목별 결과

### 1. 하드코딩 문자열 → `t()` 교체 (PASS)
- `pages/`, `widgets/`, `features/` 하위 `.tsx` 25개 파일이 `useTranslation`을 사용.
- 한국어 잔존 텍스트는 **코드 주석 6건뿐**(redirect-route.tsx 2, stock-take.tsx 4) — UI 문자열 아님, 무해.
- 영문 하드코딩 JSX 텍스트 없음. `<title>Swagger</title>`(navbar-left.tsx), `<title>GitHub</title>`(welcome.tsx)는 SVG 아이콘 접근성 라벨이자 고유명사 — 번역 대상 아님.
- `alert()`/`confirm()` 호출 모두 `t(...)` 사용 확인.

### 2. 동적 보간(`{{variable}}`) (PASS)
- `t('page.orderDetail.orderTitle', { id })`, `t('page.purchaseOrderDetail.poTitle', { id })`,
  `t('page.companyDetail.taxBadge', { status })`, `t('page.stockTake.savedCount', { count })`,
  `...deleteConfirm` `{ name }` / `{ firstName, lastName }` 등 정상 사용.
- `purchase-order-detail.tsx`의 동적 액션 키 `t(\`page.purchaseOrderDetail.actions.${actionKey}\`)`가
  참조하는 5개 접미사(requestApproval/approve/receive/markAsPaid/reject) **en/ko 양쪽 JSON에 모두 존재** 확인.

### 3. 백엔드 에러 메시지 비번역 원칙 (PASS)
- `result.description`(서버가 사용자 언어로 내려주는 값)을 `t()`로 재번역하지 않고,
  값으로 직접 사용하거나 `{ reason }` / `{ message }` 보간 인자로만 전달.
- description이 비었을 때의 클라이언트 fallback만 `t('...unknownError')` 등 번역 키 사용 — 원칙 준수.

### 4. en/ko JSON 키 구조 일치 (PASS)
- en/ko 각각 **500개 키, 차이 0건**(EN-only/KO-only 모두 빈 집합).
- 소스에서 사용된 정적 `t()` 키 전수 추출 → **JSON에 누락된 키 0건**(런타임 키 미스 위험 없음).

### 5. TypeScript 컨벤션 (PASS)
- `verbatimModuleSyntax`: 타입 전용 import `import type { TFunction } from "i18next"` 정상 사용(admin-user/admin-team/admin-category/title).
- 모듈 레벨 DataGrid 컬럼/헬퍼는 `t`를 인자(`TFunction`)로 받아 모듈 스코프에서 훅을 호출하는 위반 없음.
- `noUnusedLocals`/`noUnusedParameters`: 빌드(`tsc -b`) 통과로 위반 없음 확인. `as` 강제 캐스팅 추가 없음.

### 6. FSD / 초기화 (PASS)
- `main.tsx` 최상단 `import "./shared/i18n";` — App 렌더 전 초기화.
- `shared/i18n.ts`: `react: { useSuspense: false }` 포함으로 번들 로드 중 Suspense 미설정 컴포넌트 깨짐 방지.
- `entities/` 외부 `~Ifs` 인라인 정의 추가 없음, 레이어 역참조 없음.

## 빌드 결과
- 명령: `cd frontend; npm run build` (`tsc -b && vite build`)
- 결과: **PASS** — 1943 modules transformed, built in ~5.2s, 타입 오류 0건.
- 경고: 청크 사이즈(>500kB), MUI exceljs `direct eval` — 모두 i18n과 무관한 기존 경고.
- (PowerShell 출력의 NativeCommandError 라인은 vite reporter의 stderr 래핑일 뿐 실제 오류 아님.)

## 수정된 내용
없음. 원본 구현이 컨벤션을 준수하여 수정이 불필요했다.

## Minor (잔존, 조치 불필요)
- `company.contactPanel.alerts.unknownError` 키가 코드 단순화로 더 이상 참조되지 않음(en/ko 양쪽 잔존).
  빌드/런타임 무해하며 en/ko 대칭이 유지되므로 제거하지 않음. 향후 정리 가능.
