# 관리자 DataGrid CRUD 페이지 템플릿

`<Domain>` = PascalCase (예: `Order`), `<domain>` = camelCase/경로 (예: `order`)

## 1. 타입 정의 (`entities/<domain>.ts`)

```typescript
export interface <Domain>Ifs {
  id: number;
  name: string;
  isNew?: boolean; // DataGrid 신규 행 구분용 (POST vs PUT 분기)
}
```

## 2. 페이지 (`pages/admin-<domain>.tsx`)

```typescript
import { DataGrid, type GridDataSource } from "@mui/x-data-grid";
import { dataGridInitialState, defaultColOptions } from "@/features/data-grid/constants";
import { ActionHandlersContext } from "@/features/data-grid/action-context";
import { ActionsCell } from "@/features/data-grid/actions-cell";
import { privateApi } from "@/shared/api";
import type { ApiIfs, PageIfs } from "@/entities/app/api";
import type { <Domain>Ifs } from "@/entities/<domain>";
import { PageWrapper, Title } from "@/shared/ui/global-styles";

export function Admin<Domain>() {
  const dataSource: GridDataSource = {
    getRows: async (params) => {
      const { page, pageSize } = params.paginationModel;
      const res = await privateApi.get("/v1/admin/<domain>", { params: { page, size: pageSize } });
      const data: ApiIfs<PageIfs<<Domain>Ifs>> = res.data;
      return { rows: data.body?.content ?? [], rowCount: data.body?.page.totalElements ?? 0 };
    },
  };

  const processRowUpdate = async (newRow: <Domain>Ifs) => {
    if (newRow.isNew) {
      const res = await privateApi.post("/v1/admin/<domain>", { name: newRow.name });
      const data: ApiIfs<<Domain>Ifs> = res.data;
      return data.body ?? newRow;
    }
    await privateApi.put(`/v1/admin/<domain>/${newRow.id}`, { name: newRow.name });
    return newRow;
  };

  const handleDeleteClick = (id: number) => {
    privateApi.delete(`/v1/admin/<domain>/${id}`).catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      console.error(data?.result?.description ?? "삭제 중 오류가 발생했습니다.");
    });
  };

  const columns = [
    { field: "id", headerName: "ID", width: 80 },
    { field: "name", headerName: "이름", flex: 1, editable: true },
    { field: "actions", type: "actions", renderCell: () => <ActionsCell /> },
  ];

  return (
    <PageWrapper>
      <Title><Domain> 관리</Title>
      <ActionHandlersContext value={{ handleDeleteClick }}>
        <DataGrid
          dataSource={dataSource}
          columns={columns}
          columnDefaults={defaultColOptions}
          initialState={dataGridInitialState}
          processRowUpdate={processRowUpdate}
          editMode="row"
        />
      </ActionHandlersContext>
    </PageWrapper>
  );
}
```

## 3. 라우터 등록 (`app/router.tsx`)

```typescript
{
  path: "/admin/<domain>",
  element: (
    <AdminRoute>
      <Admin<Domain> />
    </AdminRoute>
  ),
}
```

가드는 권한 요구사항에 맞게 선택한다 (`SKILL.md`의 "라우트 가드 선택" 표 참고).

## 4. 상세/폼 페이지가 필요한 경우

`profile.tsx`의 필드별 핸들러 팩토리 패턴을 따른다:

```typescript
const updateField = (field: keyof <Domain>Ifs) =>
  (e: React.ChangeEvent<HTMLInputElement>) => {
    set<Domain>((prev) => (prev ? { ...prev, [field]: e.target.value } : prev));
  };

const onChangeName = updateField("name");
```

폼 제출 시 빈 문자열은 `convertEmptyStringToNull()`(`shared/utils`)로 변환 후 전송한다.
