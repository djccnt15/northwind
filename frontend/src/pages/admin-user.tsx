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

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto 1fr;
`;

const columns: GridColDef[] = [
  { field: "id", headerName: "DB ID", width: 70 },
  { field: "username", headerName: "ID", width: 130 },
  { field: "email", headerName: "Email", width: 200 },
  { field: "authorities", headerName: "Role", width: 100 },
];

export default function AdminUser() {
  const customDataSource: GridDataSource = {
    getRows: async (params: GridGetRowsParams) => {
      const res = await privateApi.get("/v1/admin/user/users", { params });
      const data: ApiIfs<ListCountIfs<UserIfs>> = res.data;
      const users = data.body?.list ?? [];
      const totalCounts = data.body?.totalCounts ?? 0;

      return {
        rows: users,
        rowCount: totalCounts,
      };
    },
  };

  return (
    <Wrapper>
      <Title>Admin User</Title>
      <DataGrid
        columns={columns}
        dataSource={customDataSource}
        pagination
        initialState={{
          pagination: {
            paginationModel: { pageSize: 10, page: 0 },
            rowCount: 0,
          },
        }}
        pageSizeOptions={[10, 20, 50]}
      />
    </Wrapper>
  );
}
