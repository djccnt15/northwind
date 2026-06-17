# 구현 계획

## 백엔드 작업 항목

없음 (프론트엔드 전용)

## 프론트엔드 작업 항목

### 1. 패키지 설치
```bash
cd frontend
npm install i18next i18next-browser-languagedetector i18next-http-backend
```

### 2. 신규 파일 생성

| 파일 | 내용 |
|------|------|
| `frontend/public/locales/en/translation.json` | 영어 번역 JSON (기준) |
| `frontend/public/locales/ko/translation.json` | 한국어 번역 JSON |
| `frontend/src/shared/i18n.ts` | i18next 초기화 모듈 |

#### `frontend/src/shared/i18n.ts` 구조
```typescript
import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import Backend from "i18next-http-backend";

i18n
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: "en",
    supportedLngs: ["ko", "en"],
    defaultNS: "translation",
    backend: { loadPath: "/locales/{{lng}}/{{ns}}.json" },
    interpolation: { escapeValue: false },
  });

export default i18n;
```

### 3. 수정 파일 (26개)

#### `frontend/src/main.tsx`
- 최상단에 `import "./shared/i18n";` 추가 (App 렌더링 전 초기화)

#### `frontend/src/widgets/navbar-left.tsx`
- nav 라벨 전체 → `t('nav.*')` 교체

#### pages/ (19개)
| 파일 | 주요 번역 대상 |
|------|--------------|
| login.tsx | 제목, placeholder, 버튼, 에러 메시지 |
| signup.tsx | 제목, placeholder, 버튼, 모달, 에러 메시지 |
| home.tsx | 환영 메시지 |
| welcome.tsx | 소개 텍스트 전체 |
| profile.tsx | 라벨, placeholder, 버튼, alert 메시지 |
| admin-user.tsx | 테이블 헤더, 버튼, confirm/alert 메시지 |
| admin-team.tsx | 제목, 헤더, alert 메시지 |
| admin-category.tsx | 제목, 헤더, alert 메시지 |
| title.tsx | 제목, 헤더, alert 메시지 |
| products.tsx | 제목, 필터 라벨, 헤더 |
| product-detail.tsx | 제목, 라벨, 버튼, alert/confirm 메시지 |
| companies.tsx | 제목, placeholder, 헤더 |
| company-detail.tsx | 제목, 라벨, 버튼, 테이블 헤더, alert/confirm 메시지 |
| orders.tsx | 제목, placeholder, 헤더 |
| order-detail.tsx | 제목, 라벨, 테이블 헤더, alert 메시지 |
| order-new.tsx | 제목, 라벨, 버튼, alert 메시지 |
| purchase-orders.tsx | 제목, placeholder, 헤더 |
| purchase-order-detail.tsx | 제목, 라벨, 버튼, 모달, alert/confirm 메시지 |
| purchase-order-new.tsx | 제목, 라벨, 버튼, alert 메시지 |
| stock-take.tsx | 전체(한국어 → 번역 키 교체) |

#### features/ (4개)
| 파일 | 주요 번역 대상 |
|------|--------------|
| data-grid/quick-toolbar.tsx | 툴바 버튼, placeholder |
| data-grid/crud-cell.tsx | Save/Cancel/Edit/Delete |
| company/company-create-modal.tsx | 제목, 라벨, 버튼, alert 메시지 |
| company/contact-panel.tsx | 제목, 라벨, 버튼, confirm/alert 메시지 |

### 4. 번역 키 구조 (단일 `translation` 네임스페이스)

```json
{
  "nav": {
    "home": "", "profile": "", "companies": "", "products": "",
    "stockTake": "", "orders": "", "purchaseOrders": "",
    "admin": { "user": "", "title": "", "team": "", "category": "" },
    "openApi": "", "logout": ""
  },
  "auth": {
    "login": {
      "title": "", "idPlaceholder": "", "passwordPlaceholder": "",
      "submit": "", "submitting": "", "loading": "",
      "rememberMe": "", "rememberId": "",
      "failedDefault": "Login failed. Please check your username and password.",
      "failedWithReason": "Login failed. {{reason}}",
      "noAccount": "", "createOne": ""
    },
    "signup": {
      "title": "", "idPlaceholder": "", "emailPlaceholder": "",
      "passwordPlaceholder": "", "confirmPasswordPlaceholder": "",
      "checkEmail": "", "submit": "", "submitting": "",
      "checkEmailHint": "", "emailEmptyError": "", "passwordMismatch": "",
      "signupFailed": "", "noRoles": "",
      "successMessage": "", "successLink": "", "failedModal": ""
    }
  },
  "page": {
    "home": { "title": "Welcome to Northwind! {{username}}", "description": "" },
    "welcome": { "title": "", "desc1": "", "desc2": "", "desc3": "", "login": "", "signUp": "", "slogan": "", "footer": "" },
    "profile": { ... 필드/버튼/alert 모두 },
    "adminUser": { ... },
    "adminTeam": { ... },
    "adminCategory": { ... },
    "title": { ... },
    "products": { ... },
    "productDetail": { ... },
    "companies": { ... },
    "companyDetail": { ... },
    "orders": { ... },
    "orderDetail": { ... },
    "orderNew": { ... },
    "purchaseOrders": { ... },
    "purchaseOrderDetail": { ... },
    "purchaseOrderNew": { ... },
    "stockTake": {
      "title": "", "keywordLabel": "", "searchPlaceholder": "",
      "search": "", "saveBtn": "",
      "col": { "productCode": "", "productName": "", "systemQty": "", "actualQty": "", "diff": "" },
      "savedCount": "{{count}}개 항목이 저장되었습니다.",
      "validationError": "", "saveError": ""
    }
  },
  "dataGrid": {
    "save": "", "cancel": "", "edit": "", "delete": "",
    "addRecord": "", "columns": "", "export": "", "search": "",
    "searchPlaceholder": "", "clearSearch": "",
    "print": "", "downloadCsv": ""
  },
  "company": {
    "createModal": { ... },
    "contactPanel": { ... }
  },
  "common": {
    "loading": "", "back": "", "backToList": "",
    "save": "", "cancel": "", "edit": "", "delete": "",
    "search": "", "all": "", "create": "",
    "notFound": "", "unknownError": "",
    "status": { "active": "", "discontinued": "" }
  }
}
```

동적 보간:
- `t('page.home.title', { username: user?.username })` → `"Welcome to Northwind! {{username}}"`
- `t('auth.login.failedWithReason', { reason: description })` → `"Login failed. {{reason}}"`
- `t('page.stockTake.savedCount', { count: saved.length })` → `"{{count}}개 항목이 저장되었습니다."`

## 참고 패턴

신규 인프라 도입으로 기존 참고 패턴 없음. react-i18next 공식 패턴 적용:
```typescript
import { useTranslation } from "react-i18next";
const { t } = useTranslation();
```

## doc/ 영향 범위

- doc/StoryBoard.md: 없음 (UI 텍스트 다국어화는 화면 구현 상태 변경 없음)
- doc/PRD.md / doc/EDR.md: 없음

## QA 중점 검토 항목

1. TypeScript 빌드 통과 (`npm run build`)
2. 모든 하드코딩 문자열이 번역 키로 교체되었는지
3. 동적 보간(`{{variable}}`) 누락 없는지
4. stock-take.tsx 한국어 텍스트 전부 번역 키로 교체되었는지
5. `import type` 규칙 위반 없는지 (verbatimModuleSyntax)
6. en/ko JSON에서 키 구조 일치 여부
