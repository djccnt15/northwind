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
import { dataGridInitialState } from "../features/data-grid/constants";
import QuickToolbar from "../features/data-grid/custom-toolbar";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto 1fr;
`;

const columns: GridColDef[] = [
  {
    field: "id",
    headerName: "DB ID",
    width: 70,
    filterable: false,
    sortable: false,
    editable: false,
  },
  { field: "username", headerName: "ID", width: 130 },
  { field: "email", headerName: "Email", width: 200 },
  { field: "authorities", headerName: "Role", width: 200 },
];

const initialState = {
  ...dataGridInitialState,
  columns: {
    columnVisibilityModel: {
      id: false,
    },
  },
};

export default function AdminUser() {
  const [isLoading, setIsLoading] = useState(false);

  const customDataSource: GridDataSource = {
    getRows: async (params: GridGetRowsParams) => {
      setIsLoading(true);
      const res = await privateApi
        .get("/v1/admin/user/users", {
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
        pagination
        pageSizeOptions={[10, 20, 50, 100]}
        showToolbar
        slots={{ toolbar: QuickToolbar }}
        slotProps={{
          toolbar: {
            debounceMs: 1000,
            expanded: true,
          },
        }}
      />
    </Wrapper>
  );
}
