import styled from "styled-components";
import { useTranslation } from "react-i18next";
import type { TFunction } from "i18next";
import type { UserIfs } from "../entities";
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
} from "../shared/ui";
import {
  DataGrid,
  type GridDataSource,
  type GridGetRowsParams,
  useGridApiRef,
} from "@mui/x-data-grid";
import type { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import type { ApiIfs, PageIfs } from "../entities/app";
import { useCallback, useEffect, useState } from "react";
import {
  dataGridInitialState,
  defaultColOptions,
  QuickToolbar,
} from "../features/data-grid";
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
  padding: 4px 0;
  overflow-y: scroll;
`;

const FormField = styled.div``;

const FormLabel = styled.label``;

const FormInput = styled.input``;

const onResetPassword = (
  params: GridRenderCellParams<UserIfs>,
  t: TFunction,
) => {
  const { id, username } = params.row;
  const ok = confirm(t("page.adminUser.resetConfirm", { username }));
  if (!ok) return;

  privateApi
    .patch(`/v1/admin/users/${id}/reset-password`)
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

      alert(t("page.adminUser.resetSuccess", { username: updatedUsername }));
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.adminUser.unknownError");
      console.error("Failed to reset password:", message);
      alert(t("page.adminUser.resetFailed", { message }));
    })
    .finally(() => {});
};

const createColumns = (
  t: TFunction,
  onReset: (params: GridRenderCellParams<UserIfs>) => void,
  onRoleClick: (params: GridRenderCellParams<UserIfs>) => void,
  teamList: string[] = [],
  titleList: string[] = [],
): GridColDef[] => [
  {
    ...defaultColOptions,
    field: "id",
    headerName: t("page.adminUser.cols.dbId"),
    flex: 0.2,
    editable: false,
  },
  {
    ...defaultColOptions,
    field: "enabled",
    headerName: t("page.adminUser.cols.enabled"),
    flex: 0.2,
    editable: true,
    type: "boolean",
  },
  {
    ...defaultColOptions,
    field: "username",
    headerName: t("page.adminUser.cols.username"),
    flex: 0.5,
    editable: true,
  },
  {
    ...defaultColOptions,
    field: "email",
    headerName: t("page.adminUser.cols.email"),
    flex: 1,
    editable: true,
  },
  {
    ...defaultColOptions,
    field: "title",
    headerName: t("page.adminUser.cols.title"),
    type: "singleSelect",
    flex: 0.5,
    editable: true,
    valueOptions: titleList,
    valueGetter: (_value, row: UserIfs) => row.employee?.title ?? "",
    valueSetter: (value, row: UserIfs) => {
      if (!row.employee) {
        return row;
      }

      return {
        ...row,
        employee: { ...row.employee, title: String(value ?? "") },
      };
    },
  },
  {
    ...defaultColOptions,
    field: "team",
    headerName: t("page.adminUser.cols.team"),
    type: "singleSelect",
    flex: 0.5,
    editable: true,
    valueOptions: teamList,
  },
  {
    ...defaultColOptions,
    field: "roles",
    headerName: t("page.adminUser.cols.manageRoles"),
    flex: 0.2,
    headerAlign: "center",
    align: "center",
    renderCell: (params: GridRenderCellParams<UserIfs>) => (
      <CellBlueButton onClick={() => onRoleClick(params)}>
        {t("page.adminUser.rolesBtn")}
      </CellBlueButton>
    ),
  },
  {
    ...defaultColOptions,
    field: "authorities",
    headerName: t("page.adminUser.cols.role"),
    flex: 1,
  },
  {
    ...defaultColOptions,
    field: "liveUntil",
    headerName: t("page.adminUser.cols.liveUntil"),
    editable: true,
    flex: 1,
    type: "dateTime",
    valueGetter: (value) => value && new Date(value),
  },
  {
    ...defaultColOptions,
    field: "passwordChangedAt",
    headerName: t("page.adminUser.cols.passwordChangedAt"),
    flex: 1,
    type: "dateTime",
    valueGetter: (value) => value && new Date(value),
  },
  {
    ...defaultColOptions,
    field: "lastLoginAt",
    headerName: t("page.adminUser.cols.lastLoginAt"),
    flex: 1,
    type: "dateTime",
    valueGetter: (value) => value && new Date(value),
  },
  {
    ...defaultColOptions,
    field: "loginFailedCount",
    headerName: t("page.adminUser.cols.loginFailedCount"),
    flex: 0.2,
    type: "number",
  },
  {
    ...defaultColOptions,
    field: "reset",
    headerName: t("page.adminUser.cols.resetPassword"),
    flex: 0.2,
    headerAlign: "center",
    align: "center",
    renderCell: (params: GridRenderCellParams<UserIfs>) => (
      <CellBlueButton onClick={() => onReset(params)}>
        {t("page.adminUser.resetBtn")}
      </CellBlueButton>
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
  t: TFunction,
): Promise<UserIfs> => {
  return await privateApi
    .patch(`/v1/admin/users/${originalRow.id}/profile`, {
      username: updatedRow.username,
      email: updatedRow.email,
      isEnabled: updatedRow.enabled,
      liveUntil: updatedRow.liveUntil
        ? new Date(updatedRow.liveUntil).toISOString()
        : null,
      team: updatedRow.team,
      title: updatedRow.employee?.title ?? null,
    })
    .then((res) => {
      const data: ApiIfs<UserIfs> = res.data;
      return data.body ?? updatedRow;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.adminUser.unknownError");
      console.error("Failed to update user:", message);
      alert(t("page.adminUser.updateFailed", { message }));
      return originalRow;
    });
};

export default function AdminUser() {
  const { t } = useTranslation();
  const apiRef = useGridApiRef();

  const processRowUpdate = async (
    updatedRow: UserIfs,
    originalRow: UserIfs,
  ): Promise<UserIfs> => {
    return await onEdit(updatedRow, originalRow, t);
  };
  const [roles, setRoles] = useState<string[]>([]);
  const [teamList, setTeamList] = useState<string[]>([]);
  const [titleList, setTitleList] = useState<string[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);

  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);

  const closeModal = useCallback(() => {
    setShowModal(false);
    setSelectedRoles([]);
    setSelectedUserId(null);
  }, []);

  useKeyDown("Escape", closeModal);

  const onModalOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      closeModal();
    }
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
      alert(t("page.adminUser.noUserSelected"));
      return;
    }

    privateApi
      .patch(`/v1/admin/users/${selectedUserId}/roles`, {
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

        alert(t("page.adminUser.rolesUpdated"));
        closeModal();
      })
      .catch((err) => {
        console.error("Failed to update roles:", err);
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description || t("page.adminUser.unknownError");
        alert(t("page.adminUser.rolesFailed", { message }));
      });
  };

  useEffect(() => {
    privateApi
      .get("/v1/admin/roles")
      .then((res) => {
        const data: ApiIfs<string[]> = res.data;
        setRoles(data.body ?? []);
      })
      .catch((err) => {
        console.error("Failed to fetch roles:", err);
      });
  }, []);

  useEffect(() => {
    privateApi
      .get("/v1/admin/teams/all")
      .then((res) => {
        const data: ApiIfs<string[]> = res.data;
        const teamList = data.body ?? [];
        setTeamList(teamList);
      })
      .catch((err) => {
        console.error("Failed to fetch teams:", err);
      });
  }, []);

  useEffect(() => {
    privateApi
      .get("/v1/admin/titles/all")
      .then((res) => {
        const data: ApiIfs<string[]> = res.data;
        const titleList = data.body ?? [];
        setTitleList(titleList);
      })
      .catch((err) => {
        console.error("Failed to fetch titles:", err);
      });
  }, []);

  const columns = createColumns(
    t,
    (params) => onResetPassword(params, t),
    (params) => {
      setSelectedUserId(params.row.id);
      setSelectedRoles(params.row.authorities ?? []);
      setShowModal(true);
    },
    teamList,
    titleList,
  );

  const customDataSource: GridDataSource = {
    getRows: async (params: GridGetRowsParams) => {
      setIsLoading(true);
      const res = await privateApi
        .get("/v1/admin/users", {
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

      const data: ApiIfs<PageIfs<UserIfs>> = res?.data;
      const users = data?.body?.content ?? [];
      const totalCounts = data?.body?.page?.totalElements ?? 0;

      return {
        rows: users,
        rowCount: totalCounts,
      };
    },
  };

  return (
    <Wrapper>
      <Title>{t("page.adminUser.title")}</Title>
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
            toolbarName: t("page.adminUser.toolbarName"),
            debounceMs: 1000,
            expanded: true,
          },
        }}
      />
      {showModal && (
        <ModalOverlay onClick={onModalOverlayClick}>
          <RoleModal>
            <ModalTitleArea>
              <ModalTitle>{t("page.adminUser.roleManagement")}</ModalTitle>
              <ModalBtnArea>
                <CellBlueButton form="role-form">
                  {t("page.adminUser.save")}
                </CellBlueButton>
                <CellRedButton type="button" onClick={closeModal}>
                  {t("page.adminUser.cancel")}
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
                <FormLabel>{t("page.adminUser.noRoles")}</FormLabel>
              )}
            </RoleForm>
          </RoleModal>
        </ModalOverlay>
      )}
    </Wrapper>
  );
}
