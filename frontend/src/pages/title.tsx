import AddIcon from "@mui/icons-material/Add";
import { Box, Tooltip, Typography } from "@mui/material";
import {
  DataGrid,
  GridRowEditStopReasons,
  GridRowModes,
  Toolbar,
  ToolbarButton,
  type GridColDef,
  type GridEventListener,
  type GridRowId,
  type GridRowModesModel,
  type GridSlotProps,
  type GridSlots,
} from "@mui/x-data-grid";
import { randomId } from "@mui/x-data-grid-generator";
import { useCallback, useEffect, useMemo, useState } from "react";
import styled from "styled-components";
import { useTranslation } from "react-i18next";
import type { TFunction } from "i18next";
import type { ApiIfs, PageIfs } from "../entities/app";
import type { TitleIfs } from "../entities";
import {
  ActionHandlersContext,
  type ActionHandlers,
  dataGridInitialState,
  ActionsCell,
} from "../features/data-grid";
import { privateApi } from "../shared/api";
import { PageWrapper, Title } from "../shared/ui";

const Wrapper = styled(PageWrapper)``;

const createColumns = (t: TFunction): GridColDef[] => [
  { field: "title", headerName: t("page.title.col.titleName"), flex: 1, editable: true },
  {
    field: "actions",
    type: "actions",
    headerName: t("page.title.col.actions"),
    width: 100,
    cellClassName: "actions",
    renderCell: (params) => <ActionsCell {...params} />,
  },
];

const createTitle = async (
  updatedTitle: TitleIfs,
  originalTitle: TitleIfs,
  t: TFunction,
): Promise<TitleIfs> => {
  return await privateApi
    .post("/v1/admin/titles", { title: updatedTitle.title })
    .then((res) => {
      const data: ApiIfs<TitleIfs> = res.data;
      return data.body ?? updatedTitle;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.title.unknownError");
      console.error("Failed to create title:", message);
      alert(t("page.title.createFailed", { message }));
      return originalTitle;
    });
};

const updateTitle = async (
  updatedTitle: TitleIfs,
  originalTitle: TitleIfs,
  t: TFunction,
): Promise<TitleIfs> => {
  return await privateApi
    .put(`/v1/admin/titles/${updatedTitle.id}`, {
      title: updatedTitle.title,
    })
    .then((res) => {
      const data: ApiIfs<TitleIfs> = res.data;
      return data.body ?? updatedTitle;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.title.unknownError");
      console.error("Failed to update title:", message);
      alert(t("page.title.updateFailed", { message }));
      return originalTitle;
    });
};

const onEdit = async (
  updatedRow: TitleIfs,
  originalRow: TitleIfs,
  t: TFunction,
): Promise<TitleIfs> => {
  if (updatedRow.isNew) {
    return await createTitle(updatedRow, originalRow, t);
  }

  return await updateTitle(updatedRow, originalRow, t);
};

const onDelete = async (id: number, t: TFunction): Promise<void> => {
  await privateApi
    .delete(`/v1/admin/titles/${id}`)
    .then(() => {
      console.log("Title deleted successfully");
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.title.unknownError");
      console.error("Failed to delete title:", message);
      alert(t("page.title.deleteFailed", { message }));
    });
};

function EditToolbar(props: GridSlotProps["toolbar"]) {
  const { setRows, setRowModesModel } = props;
  const { t } = useTranslation();

  const handleClick = () => {
    const id = randomId();
    setRows((oldRows) => [...oldRows, { id, title: "", isNew: true }]);
    setRowModesModel((oldModel) => ({
      ...oldModel,
      [id]: { mode: GridRowModes.Edit, fieldToFocus: "title" },
    }));
  };

  return (
    <Toolbar>
      <Typography sx={{ fontWeight: "medium", flex: 1, mx: 0.5 }}>
        {props.toolbarName}
      </Typography>
      <Tooltip title={t("page.title.addRecord")}>
        <ToolbarButton onClick={handleClick}>
          <AddIcon fontSize="small" />
        </ToolbarButton>
      </Tooltip>
    </Toolbar>
  );
}

export default function EmployeeTitle() {
  const { t } = useTranslation();
  const columns = createColumns(t);
  const [rows, setRows] = useState<TitleIfs[]>([]);
  const [rowModesModel, setRowModesModel] = useState<GridRowModesModel>({});
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState(
    dataGridInitialState.pagination.paginationModel,
  );

  const fetchTitles = useCallback(async (page: number, size: number) => {
    setLoading(true);

    await privateApi
      .get("/v1/admin/titles", { params: { page, size } })
      .then((res) => {
        const data: ApiIfs<PageIfs<TitleIfs>> = res.data;
        setRows(data?.body?.content ?? []);
        setRowCount(data?.body?.page?.totalElements ?? 0);
      })
      .catch((error) => {
        console.error("Error fetching employee titles:", error);
        alert(t("page.title.fetchFailed"));
      })
      .finally(() => {
        setLoading(false);
      });
  }, [t]);

  useEffect(() => {
    queueMicrotask(() => {
      void fetchTitles(paginationModel.page, paginationModel.pageSize);
    });
  }, [fetchTitles, paginationModel.page, paginationModel.pageSize]);

  const handleRowEditStop: GridEventListener<"rowEditStop"> = (
    params,
    event,
  ) => {
    if (params.reason === GridRowEditStopReasons.rowFocusOut) {
      event.defaultMuiPrevented = true;
    }
  };

  const actionHandlers = useMemo<ActionHandlers>(
    () => ({
      handleEditClick: (id: GridRowId) => {
        setRowModesModel((prevRowModesModel) => ({
          ...prevRowModesModel,
          [id]: { mode: GridRowModes.Edit },
        }));
      },
      handleSaveClick: (id: GridRowId) => {
        setRowModesModel((prevRowModesModel) => ({
          ...prevRowModesModel,
          [id]: { mode: GridRowModes.View },
        }));
      },
      handleDeleteClick: (id: GridRowId) => {
        setRows((prevRows) => prevRows.filter((row) => row.id !== id));
        void onDelete(Number(id), t).then(() =>
          fetchTitles(paginationModel.page, paginationModel.pageSize),
        );
      },
      handleCancelClick: (id: GridRowId) => {
        setRowModesModel((prevRowModesModel) => {
          return {
            ...prevRowModesModel,
            [id]: { mode: GridRowModes.View, ignoreModifications: true },
          };
        });

        setRows((prevRows) => {
          const editedRow = prevRows.find((row) => row.id === id);
          if (editedRow!.isNew) {
            return prevRows.filter((row) => row.id !== id);
          }
          return prevRows;
        });
      },
    }),
    [fetchTitles, paginationModel.page, paginationModel.pageSize, t],
  );

  const processRowUpdate = async (
    updatedRow: TitleIfs,
    originalRow: TitleIfs,
  ): Promise<TitleIfs> => {
    const savedRow = await onEdit(updatedRow, originalRow, t);
    const normalizedRow = { ...savedRow, isNew: false };

    setRows((prevRows) =>
      prevRows.map((row) => (row.id === originalRow.id ? normalizedRow : row)),
    );

    if (savedRow.id !== originalRow.id) {
      setRowModesModel((prevRowModesModel) => {
        const rest = { ...prevRowModesModel };
        delete rest[originalRow.id];
        return rest;
      });
    }

    return normalizedRow;
  };

  return (
    <Wrapper>
      <Title>{t("page.title.title")}</Title>
      <Box
        sx={{
          height: "100%",
          width: "100%",
          overflowY: "auto",
          "& .actions": {
            color: "text.secondary",
          },
          "& .textPrimary": {
            color: "text.primary",
          },
        }}
      >
        <ActionHandlersContext value={actionHandlers}>
          <DataGrid
            rows={rows}
            columns={columns}
            editMode="row"
            rowModesModel={rowModesModel}
            loading={loading}
            pagination
            paginationMode="server"
            rowCount={rowCount}
            paginationModel={paginationModel}
            onPaginationModelChange={setPaginationModel}
            pageSizeOptions={[10, 20, 50, 100]}
            onRowModesModelChange={setRowModesModel}
            onRowEditStop={handleRowEditStop}
            processRowUpdate={processRowUpdate}
            showToolbar
            slots={{ toolbar: EditToolbar as GridSlots["toolbar"] }}
            slotProps={{
              toolbar: {
                toolbarName: t("page.title.toolbarName"),
                setRows,
                setRowModesModel,
              },
            }}
          />
        </ActionHandlersContext>
      </Box>
    </Wrapper>
  );
}
