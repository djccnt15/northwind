import styled from "styled-components";
import type { UserIfs } from "../entities/app/user";
import { privateApi } from "../shared/api";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnHoverTomatoRed,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  commBtnTomatoRed,
  commBtnTomatoRedBoxShadow,
  globalTransition,
  ModalDefault,
  ModalOverlay,
  PageWrapper,
  Title,
} from "../shared/ui/global-styles";
import {
  DataGrid,
  type GridDataSource,
  type GridGetRowsParams,
  useGridApiRef,
} from "@mui/x-data-grid";
import type { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import type { ApiIfs, ListCountIfs } from "../entities/app/api";
import { useCallback, useEffect, useState } from "react";
import {
  dataGridInitialState,
  defaultColOptions,
} from "../features/data-grid/constants";
import QuickToolbar from "../features/data-grid/custom-toolbar";
import { useKeyDown } from "../shared/useKeyDown";

const Wrapper = styled(PageWrapper)``;

const CellBlueButton = styled.button`
  ${commBtnSkyBlue}
  ${globalTransition}
  ${commBorderRadius}
  height: 30px;
  width: 40px;
  border: none;
  color: white;
  font-size: 10px;
  cursor: pointer;

  &:hover {
    ${commBtnHoverSkyBlue}
    ${globalTransition}
  }

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const CellRedButton = styled.button`
  ${commBtnTomatoRed}
  ${globalTransition}
  ${commBorderRadius}
  height: 30px;
  width: 40px;
  border: none;
  color: white;
  font-size: 10px;
  cursor: pointer;

  &:hover {
    ${commBtnHoverTomatoRed}
    ${globalTransition}
  }

  &:focus {
    outline: none;
    ${commBtnTomatoRedBoxShadow}
  }
`;

const RoleModal = styled(ModalDefault)`
  width: 400px;
  background-color: white;
  display: grid;
  grid-template-rows: auto 1fr;
  padding: 20px;
  gap: 20px;
  max-height: 50vh;
`;

const ModalTitleArea = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const ModalTitle = styled.div`
  font-size: 18px;
  font-weight: 600;
`;

const ModalBtnArea = styled.div`
  display: flex;
  gap: 10px;
`;

const RoleForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: scroll;
`;

const FormField = styled.div``;

const FormLabel = styled.label``;

const FormInput = styled.input``;

const onResetPassword = (params: GridRenderCellParams<UserIfs>) => {
  const { id, username } = params.row;
  const ok = confirm(`Reset password for user ${username}?`);
  if (!ok) return;

  privateApi
    .patch(`/v1/admin/user/${id}/reset-password`)
    .then((res) => {
      const data: ApiIfs<UserIfs> = res.data;
      const updatedUsername = data.body?.username || "Unknown";

      if (data.body) {
        params.api.updateRows([
          {
            id: data.body.id,
            passwordChangedAt: data.body.passwordChangedAt ?? null,
            loginFailedCount: data.body.loginFailedCount ?? 0,
          },
        ]);
      }

      alert(`Reset password for user ID: ${updatedUsername}`);
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to reset password:", message);
      alert(`Failed to reset password: ${message}`);
    })
    .finally(() => {});
};

const createColumns = (
  onReset: (params: GridRenderCellParams<UserIfs>) => void,
  onRoleClick: (params: GridRenderCellParams<UserIfs>) => void,
): GridColDef[] => [
  {
    ...defaultColOptions,
    field: "id",
    headerName: "DB ID",
    flex: 0.2,
    editable: false,
  },
  {
    ...defaultColOptions,
    field: "enabled",
    headerName: "Enabled",
    flex: 0.2,
    editable: true,
    type: "boolean",
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
    field: "roles",
    headerName: "Manage Roles",
    flex: 0.2,
    headerAlign: "center",
    align: "center",
    renderCell: (params: GridRenderCellParams<UserIfs>) => (
      <CellBlueButton onClick={() => onRoleClick(params)}>Roles</CellBlueButton>
    ),
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
    field: "lastLoginAt",
    headerName: "Last Login At",
    flex: 1,
    type: "dateTime",
    valueGetter: (value) => value && new Date(value),
  },
  {
    ...defaultColOptions,
    field: "loginFailedCount",
    headerName: "Login Failed Count",
    flex: 0.2,
    type: "number",
  },
  {
    ...defaultColOptions,
    field: "reset",
    headerName: "Reset Password",
    flex: 0.2,
    headerAlign: "center",
    align: "center",
    renderCell: (params: GridRenderCellParams<UserIfs>) => (
      <CellBlueButton onClick={() => onReset(params)}>Reset</CellBlueButton>
    ),
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
  const apiRef = useGridApiRef();
  const [isLoading, setIsLoading] = useState(false);
  const [roles, setRoles] = useState<string[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);

  const closeModal = useCallback(() => {
    setShowModal(false);
    setSelectedRoles([]);
    setSelectedUserId(null);
  }, []);

  useKeyDown("Escape", closeModal);

  const handleContentClick = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
  };

  const toggleRole = (role: string, checked: boolean) => {
    setSelectedRoles((prev) => {
      if (checked) {
        return prev.includes(role) ? prev : [...prev, role];
      }
      return prev.filter((item) => item !== role);
    });
  };

  const onRoleUpdate = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (selectedUserId === null) {
      alert("No user selected");
      return;
    }

    privateApi
      .patch(`/v1/admin/user/${selectedUserId}/roles`, {
        list: selectedRoles,
      })
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;

        apiRef.current?.updateRows([
          {
            id: data.body?.id ?? selectedUserId,
            authorities: data.body?.authorities ?? selectedRoles,
          },
        ]);

        alert("Roles updated successfully");
        closeModal();
      })
      .catch((err) => {
        console.error("Failed to update roles:", err);
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to update roles: ${message}`);
      });
  };

  useEffect(() => {
    privateApi
      .get("/v1/admin/role/all")
      .then((res) => {
        const data: ApiIfs<string[]> = res.data;
        setRoles(data.body ?? []);
      })
      .catch((err) => {
        console.error("Failed to fetch roles:", err);
      });
  }, []);

  const columns = createColumns(
    (params) => onResetPassword(params),
    (params) => {
      setSelectedUserId(params.row.id);
      setSelectedRoles(params.row.authorities ?? []);
      setShowModal(true);
    },
  );

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
        apiRef={apiRef}
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
      {showModal && (
        <ModalOverlay onClick={closeModal}>
          <RoleModal onClick={handleContentClick}>
            <ModalTitleArea>
              <ModalTitle>Role Management</ModalTitle>
              <ModalBtnArea>
                <CellBlueButton form="role-form">Save</CellBlueButton>
                <CellRedButton type="button" onClick={closeModal}>
                  Cancel
                </CellRedButton>
              </ModalBtnArea>
            </ModalTitleArea>
            <RoleForm id="role-form" onSubmit={onRoleUpdate}>
              <input type="hidden" name="userId" value={selectedUserId ?? ""} />
              {Array.isArray(roles) && roles.length > 0 ? (
                roles.map((role) => (
                  <FormField key={role}>
                    <FormInput
                      type="checkbox"
                      id={role}
                      checked={selectedRoles.includes(role)}
                      onChange={(e) => toggleRole(role, e.target.checked)}
                    />
                    <FormLabel htmlFor={role}>{role}</FormLabel>
                  </FormField>
                ))
              ) : (
                <FormLabel>No roles available</FormLabel>
              )}
            </RoleForm>
          </RoleModal>
        </ModalOverlay>
      )}
    </Wrapper>
  );
}
