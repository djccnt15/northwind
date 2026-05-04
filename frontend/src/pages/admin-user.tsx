import styled from "styled-components";
import type { UserIfs } from "../entities/app/user";
import { privateApi } from "../shared/api";
import { Title } from "../shared/global-styles";
import {
  DataGrid,
  type GridDataSource,
  type GridGetRowsParams,
} from "@mui/x-data-grid";
import type { GridColDef } from "@mui/x-data-grid";
import type { ApiIfs, ListCountIfs } from "../entities/app/api";
import { useState } from "react";
import {
  dataGridInitialState,
  defaultColOptions,
} from "../features/data-grid/constants";
import QuickToolbar from "../features/data-grid/custom-toolbar";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto 1fr;
`;

const columns: GridColDef[] = [
  {
    ...defaultColOptions,
    field: "id",
    headerName: "DB ID",
    flex: 0.2,
    editable: false,
  },
  {
    ...defaultColOptions,
    field: "username",
    headerName: "ID",
    flex: 0.5,
    editable: true,
  },
  {
    ...defaultColOptions,
    field: "email",
    headerName: "Email",
    flex: 1,
    editable: true,
  },
  {
    ...defaultColOptions,
    field: "authorities",
    headerName: "Role",
    flex: 1,
  },
  {
    ...defaultColOptions,
    field: "liveUntil",
    headerName: "Live Until",
    editable: true,
    flex: 1,
    type: "dateTime",
    valueGetter: (value) => value && new Date(value),
  },
  {
    ...defaultColOptions,
    field: "passwordChangedAt",
    headerName: "Password Changed At",
    flex: 1,
    type: "dateTime",
    valueGetter: (value) => value && new Date(value),
  },
  {
    ...defaultColOptions,
    field: "enabled",
    headerName: "Enabled",
    flex: 0.2,
    editable: true,
    type: "boolean",
  },
];

const initialState = {
  ...dataGridInitialState,
  columns: {
    columnVisibilityModel: {
      id: false,
    },
  },
};

const onEdit = async (
  updatedRow: UserIfs,
  originalRow: UserIfs,
): Promise<UserIfs> => {
  return await privateApi
    .patch(`/v1/admin/user/${originalRow.id}/profile`, {
      username: updatedRow.username,
      email: updatedRow.email,
      isEnabled: updatedRow.enabled,
      liveUntil: updatedRow.liveUntil
        ? new Date(updatedRow.liveUntil).toISOString()
        : null,
    })
    .then((res) => {
      const data: ApiIfs<UserIfs> = res.data;
      return data.body ?? updatedRow;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to update user:", message);
      alert(`Failed to update user: ${message}`);
      return originalRow;
    });
};

const processRowUpdate = async (
  updatedRow: UserIfs,
  originalRow: UserIfs,
): Promise<UserIfs> => {
  return await onEdit(updatedRow, originalRow);
};

export default function AdminUser() {
  const [isLoading, setIsLoading] = useState(false);

  const customDataSource: GridDataSource = {
    getRows: async (params: GridGetRowsParams) => {
      setIsLoading(true);
      const res = await privateApi
        .get("/v1/admin/user/all", {
          params: {
            page: params?.paginationModel?.page || 0,
            size: params?.paginationModel?.pageSize || 10,
            keyword: params?.filterModel?.quickFilterValues?.[0] || "",
          },
        })
        .catch((err) => {
          console.error("Failed to fetch users:", err);
          return null;
        })
        .finally(() => {
          setIsLoading(false);
        });

      const data: ApiIfs<ListCountIfs<UserIfs>> = res?.data;
      const users = data?.body?.list ?? [];
      const totalCounts = data?.body?.totalCounts ?? 0;

      return {
        rows: users,
        rowCount: totalCounts,
      };
    },
  };

  return (
    <Wrapper>
      <Title>Admin - User Management</Title>
      <DataGrid
        columns={columns}
        dataSource={customDataSource}
        loading={isLoading}
        initialState={initialState}
        editMode="row"
        processRowUpdate={processRowUpdate}
        pagination
        pageSizeOptions={[10, 20, 50, 100]}
        showToolbar
        slots={{ toolbar: QuickToolbar }}
        slotProps={{
          toolbar: {
            toolbarName: "User Management",
            debounceMs: 1000,
            expanded: true,
          },
        }}
      />
    </Wrapper>
  );
}
