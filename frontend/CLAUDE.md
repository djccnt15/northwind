# 프론트엔드 개발 참고사항 및 코딩 컨벤션

## 기술 스택

| 항목 | 라이브러리 / 버전 |
|------|-----------------|
| UI 프레임워크 | React 19 |
| 번들러 | Vite 8 |
| 언어 | TypeScript 6 |
| 스타일링 | styled-components 6 |
| UI 컴포넌트 | MUI 9 + MUI X DataGrid 9 |
| 라우팅 | React Router 7 |
| HTTP 클라이언트 | Axios 1 |
| 자동 메모이제이션 | React Compiler (Babel 플러그인) |

> **React Compiler**: `vite.config.ts`에 Babel 플러그인으로 활성화되어 있어 `useMemo`, `useCallback`, `React.memo`를 수동으로 작성할 필요가 없다.

---

## 디렉토리 구조 (Feature-Sliced Design)

```
src/
├── main.tsx                # 앱 진입점
├── app/                    # 라우터, 레이아웃, 전역 프로바이더
│   ├── App.tsx
│   ├── router.tsx
│   ├── layout.tsx
│   └── provider/           # AuthProvider, 라우트 보호 컴포넌트
├── pages/                  # 라우트에 1:1 대응하는 페이지 컴포넌트
├── widgets/                # 여러 기능을 조합한 독립 UI 블록
│   ├── index.ts            # public API
│   └── navbar-left.tsx
├── features/               # 재사용 가능한 기능 단위 모듈
│   ├── auth/               # AuthContext, useAuth, useLogout
│   │   └── index.ts        # public API
│   └── data-grid/          # MUI DataGrid 공통 컴포넌트
│       └── index.ts        # public API
├── entities/               # 타입 정의만 포함 (로직 없음)
│   ├── index.ts            # public API (employee 타입)
│   ├── app/                # ApiIfs, PageIfs, SessionIfs, ChildNodeIfs
│   │   └── index.ts        # public API
│   └── employee.ts         # UserIfs, EmployeeIfs, TitleIfs, TeamIfs
└── shared/                 # 전 레이어에서 사용하는 공유 리소스
    ├── api.ts              # Axios 인스턴스
    ├── utils.ts            # 유틸리티 함수
    ├── useKeyDown.ts       # 커스텀 훅
    └── ui/                 # 공통 스타일드 컴포넌트
        └── index.ts        # public API
```

**레이어 의존 방향**: `app` → `pages` → `widgets` → `features` → `entities` → `shared`
상위 레이어는 하위 레이어만 참조한다. 역방향 참조는 금지.

---

## 빌드 설정

```typescript
// vite.config.ts
build: {
  outDir: "../src/main/resources/static",  // Spring 정적 자원 경로로 직접 빌드
  emptyOutDir: true,
}
```

개발 서버(`npm run dev`)는 Vite 프록시 없이 직접 백엔드(`localhost:8080`)와 통신한다. Axios baseURL을 `/api`로 설정하면 브라우저가 같은 오리진으로 요청을 보낸다.

> **타입/빌드 검증 시 주의**: 루트 `tsconfig.json`은 `"files": []`에 `references`만 있는 솔루션 스타일 설정이다. `frontend/` 디렉터리에서 `npx tsc --noEmit`을 직접 실행하면 이 루트 설정을 읽어 **0개 파일을 검사하고 거짓으로 "오류 없음"을 출력**한다 (`npx tsc --noEmit --listFilesOnly`로 확인 가능). 실제 타입 검사를 하려면 반드시 `npm run build`(`tsc -b && vite build`, 프로젝트의 공식 빌드 스크립트) 또는 `npx tsc -p tsconfig.app.json --noEmit`을 사용해야 한다.

---

## API 클라이언트 패턴

`shared/api.ts`에 Axios 인스턴스가 2개 정의되어 있다.

```typescript
import { api, privateApi } from "@/shared/api";

// 공개 API — 인증 불필요 (로그인, 회원가입)
api.post("/v1/login", params);

// 인증 API — 쿠키 포함 (withCredentials: true)
privateApi.get("/v1/auth/check-session");
privateApi.patch(`/v1/user/${userId}/profile`, body);
```

### 호출 패턴

모든 API 호출은 `.then().catch().finally()` 체이닝을 사용한다.

```typescript
setIsLoading(true);
privateApi
  .patch(`/v1/user/${userId}/profile`, body)
  .then((res) => {
    const data: ApiIfs<UserIfs> = res.data;
    setUser(data.body);
  })
  .catch((err) => {
    const data: ApiIfs<null> = err.response?.data;
    setErrorMsg(data?.result?.description ?? "오류가 발생했습니다.");
  })
  .finally(() => setIsLoading(false));
```

### API 응답 타입

```typescript
// entities/app/api.ts
interface ApiIfs<TBody = Record<string, unknown>> {
  serverTime: number;
  result: { code: number; message: string; description: string };
  body: TBody | null;
}

interface PageIfs<T = Record<string, unknown>> {
  page: { size: number; page: number; totalPages: number; totalElements: number };
  content: T[];
}
```

- 성공: `result.code === 200` (생성 201)
- 세션 확인 성공: `result.code === 1200`
- 검증 오류: `result.code === 1400` → `body`에 `{ [field]: message }` Map

---

## 인증 패턴

### 전역 상태

`features/auth`의 `useAuth()` 훅으로 어디서나 접근한다.

```typescript
const { user, setUser } = useAuth();

// 로그인 여부 확인
if (!user) return <Navigate to="/login" />;

// 권한 확인
if (!user.authorities.includes("ADMIN")) return <Navigate to="/home" />;
```

### 인증 흐름

```
앱 부팅
 └─ AuthProvider 마운트
     └─ GET /v1/auth/check-session
         ├─ 성공 (code 1200) → setUser(세션 정보)
         └─ 실패 → setUser(null)
             └─ 로딩 완료 후 UI 렌더링
```

### 라우트 보호

`app/provider/redirect-route.tsx`에 3가지 가드가 정의되어 있다.

| 컴포넌트 | 조건 | 동작 |
|---------|------|------|
| `ProtectedRoute` | `user === null` | `/login`으로 리다이렉트 |
| `AdminRoute` | `ADMIN` 권한 없음 | `/home`으로 리다이렉트 |
| `ProductRoute` | `ADMIN`, `PRODUCT` 권한 모두 없음 | `/home`으로 리다이렉트 |
| `OrderRoute` | `ADMIN`, `ORDER` 권한 모두 없음 | `/home`으로 리다이렉트 |
| `ManagerRoute` | `ADMIN`, `MANAGER` 권한 모두 없음 | `/home`으로 리다이렉트 |
| `AuthRedirectRoute` | 이미 로그인 | `/home`으로 리다이렉트 |

---

## 상태 관리 패턴

### 전역 상태: Context API

전역 상태는 `AuthContext`(user 정보)만 사용한다. 그 외 상태는 페이지/컴포넌트 로컬로 관리한다.

```typescript
// Context 정의
const AuthContext = createContext<AuthContextIfs | null>(null);

// 커스텀 훅으로 래핑
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
};
```

### 로컬 상태: useState

각 페이지는 독립적으로 `useState`로 상태를 관리한다.

```typescript
const [data, setData] = useState<UserIfs | null>(null);
const [isLoading, setIsLoading] = useState(false);
const [errorMsg, setErrorMsg] = useState("");
```

### Feature-level 상태: Context

DataGrid의 CRUD 액션은 `ActionHandlersContext`(`features/data-grid/action-context.ts`)로 공유한다.

---

## 폼 처리 패턴

모든 폼은 **Controlled Components** 방식으로 구현한다.

```typescript
const [username, setUsername] = useState("");

<input
  value={username}
  onChange={(e) => setUsername(e.target.value)}
/>
```

### 여러 필드 핸들러 - 팩토리 함수 패턴

`profile.tsx`처럼 필드가 많을 때는 팩토리 함수로 핸들러를 생성한다.

```typescript
const updateField = (field: keyof EmployeeIfs) =>
  (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmployee((prev) => ({ ...prev, [field]: e.target.value }));
  };

const onChangeFirstName = updateField("firstName");
const onChangeLastName = updateField("lastName");
```

### 빈 문자열 처리

폼 제출 시 빈 문자열을 `null`로 변환하려면 `convertEmptyStringToNull()` 유틸을 사용한다.

```typescript
import { convertEmptyStringToNull } from "@/shared/utils";

const body = convertEmptyStringToNull({ firstName, lastName, email });
privateApi.patch("/v1/user/info", body);
```

### 로그인 폼 (x-www-form-urlencoded)

Spring Security form login은 JSON이 아닌 URLSearchParams로 전송한다.

```typescript
const params = new URLSearchParams();
params.append("username", username);
params.append("password", password);
params.append("remember-me", String(rememberMe));

api.post("/v1/login", params, {
  headers: { "content-type": "application/x-www-form-urlencoded" },
});
```

---

## 스타일링 패턴

### styled-components 기본

컴포넌트별 스타일은 파일 하단 또는 별도 섹션에 styled-components로 정의한다.

```typescript
const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

const Title = styled.h1`
  font-size: 24px;
  font-weight: 700;
`;
```

### 공통 CSS 재사용

`shared/ui/global-styles.ts`에 정의된 CSS 헬퍼를 `css` 태그드 템플릿으로 재사용한다.

```typescript
import { globalTransition, commBtnSkyBlue, commBtnHoverSkyBlue } from "@/shared/ui/global-styles";

const Button = styled.button`
  ${globalTransition}
  ${commBtnSkyBlue}
  &:hover { ${commBtnHoverSkyBlue} }
`;
```

### 색상 팔레트

| 상수 | 색상 | 용도 |
|------|------|------|
| `commBtnSkyBlue` | `#17c1ff` | 기본 버튼, 링크 |
| `commBtnHoverSkyBlue` | `#2397c9` | 버튼 hover |
| `commBtnTomatoRed` | `#ff4d4f` | 삭제/경고 버튼 |
| `commBtnHoverTomatoRed` | `#d9363e` | 삭제 버튼 hover |

### 공통 스타일드 컴포넌트

`shared/ui/global-styles.ts`에 재사용 가능한 컴포넌트가 정의되어 있다.

```typescript
import { PageWrapper, Title, ModalOverlay, ModalDefault } from "@/shared/ui/global-styles";
```

| 컴포넌트 | 설명 |
|---------|------|
| `PageWrapper` | 페이지 기본 컨테이너 |
| `Title` | h1 제목 스타일 |
| `ModalOverlay` | 반투명 모달 배경 |
| `ModalDefault` | 모달 기본 레이아웃 |
| `Tooltip` / `TooltipWrapper` | 마우스 호버 툴팁 |

---

## 네이밍 규칙

| 대상 | 규칙 | 예시 |
|------|------|------|
| 파일명 | kebab-case | `admin-user.tsx`, `auth-provider.tsx` |
| 컴포넌트 함수 | PascalCase | `AdminUser`, `LeftNavBar` |
| 커스텀 훅 | `use` 접두사 | `useAuth`, `useKeyDown` |
| Context | `~Context` 접미사 | `AuthContext`, `ActionHandlersContext` |
| 인터페이스 | `~Ifs` 접미사 | `SessionIfs`, `ApiIfs`, `UserIfs` |
| 스타일드 컴포넌트 | PascalCase | `Wrapper`, `Form`, `Modal` |
| 이벤트 핸들러 | `on~` 접두사 | `onSubmit`, `onChangeUsername` |
| 비동기 액션 함수 | `handle~` 접두사 | `handleSaveClick`, `handleDeleteClick` |

---

## DataGrid 패턴

MUI X DataGrid를 사용하는 페이지는 `features/data-grid/`의 공통 컴포넌트를 활용한다.

### 기본 설정

```typescript
import { dataGridInitialState, defaultColOptions } from "@/features/data-grid/constants";

<DataGrid
  initialState={dataGridInitialState}
  columnDefaults={defaultColOptions}
/>
```

### 서버 측 페이지네이션

```typescript
const dataSource: GridDataSource = {
  getRows: async (params) => {
    const { page, pageSize } = params.paginationModel;
    const res = await privateApi.get("/v1/admin/teams", { params: { page, size: pageSize } });
    const data: ApiIfs<PageIfs<TeamIfs>> = res.data;
    return { rows: data.body?.content ?? [], rowCount: data.body?.page.totalElements ?? 0 };
  },
};
```

### CRUD 액션 셀

```typescript
// ActionHandlersContext에 핸들러를 주입
<ActionHandlersContext value={{ handleEditClick, handleSaveClick, handleCancelClick, handleDeleteClick }}>
  <DataGrid columns={[...columns, { field: "actions", renderCell: () => <ActionsCell /> }]} />
</ActionHandlersContext>
```

### 신규/기존 행 구분

```typescript
// isNew: true인 행은 processRowUpdate에서 POST, 아니면 PUT
const processRowUpdate = async (newRow: TitleIfs) => {
  if (newRow.isNew) {
    await privateApi.post("/v1/admin/titles", { title: newRow.title });
  } else {
    await privateApi.put(`/v1/admin/titles/${newRow.id}`, { title: newRow.title });
  }
  return newRow;
};
```

---

## 커스텀 훅

### useKeyDown (`shared/useKeyDown.ts`)

키보드 이벤트를 전역으로 등록한다. 주로 모달 닫기(`Escape`)에 사용한다.

```typescript
import { useKeyDown } from "@/shared/useKeyDown";

useKeyDown("Escape", () => setIsModalOpen(false));
```

### useAuth (`features/auth`)

```typescript
const { user, setUser } = useAuth();
```

---

## 타입 정의 위치

- **API 응답**: `entities/app/api.ts` — `ApiIfs<T>`, `PageIfs<T>`
- **사용자/세션**: `entities/app/user.ts` — `SessionIfs`
- **도메인 데이터**: `entities/employee.ts` — `UserIfs`, `EmployeeIfs`, `TitleIfs`, `TeamIfs`
- **컴포넌트 공통**: `entities/app/app.ts` — `ChildNodeIfs` (children prop 타입)

인터페이스는 `entities/`에만 정의한다. 컴포넌트 파일에 인라인으로 정의하지 않는다.

---

## TypeScript 설정 주의사항

`tsconfig.app.json`에 다음이 활성화되어 있다.

- `noUnusedLocals`: 사용하지 않는 변수는 컴파일 오류
- `noUnusedParameters`: 사용하지 않는 함수 파라미터는 컴파일 오류
- `verbatimModuleSyntax`: 타입 전용 import에는 반드시 `import type` 사용

```typescript
// 올바른 타입 import
import type { UserIfs } from "@/entities/employee";

// 값과 타입을 같이 import
import { api, type ApiIfs } from "@/shared/api";
```
