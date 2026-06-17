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
import type { ProductCategoryIfs } from "../entities";
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
  { field: "code", headerName: t("page.adminCategory.col.code"), width: 150, editable: true },
  { field: "name", headerName: t("page.adminCategory.col.name"), width: 200, editable: true },
  { field: "description", headerName: t("page.adminCategory.col.description"), flex: 1, editable: true },
  {
    field: "actions",
    type: "actions",
    headerName: t("page.adminCategory.col.actions"),
    width: 100,
    cellClassName: "actions",
    renderCell: (params) => <ActionsCell {...params} />,
  },
];

const createCategory = async (
  updatedCategory: ProductCategoryIfs,
  originalCategory: ProductCategoryIfs,
  t: TFunction,
): Promise<ProductCategoryIfs> => {
  return await privateApi
    .post("/v1/admin/categories", {
      code: updatedCategory.code,
      name: updatedCategory.name,
      description: updatedCategory.description,
    })
    .then((res) => {
      const data: ApiIfs<ProductCategoryIfs> = res.data;
      return data.body ?? updatedCategory;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.adminCategory.unknownError");
      console.error("Failed to create category:", message);
      alert(t("page.adminCategory.createFailed", { message }));
      return originalCategory;
    });
};

const updateCategory = async (
  updatedCategory: ProductCategoryIfs,
  originalCategory: ProductCategoryIfs,
  t: TFunction,
): Promise<ProductCategoryIfs> => {
  return await privateApi
    .put(`/v1/admin/categories/${updatedCategory.id}`, {
      code: updatedCategory.code,
      name: updatedCategory.name,
      description: updatedCategory.description,
    })
    .then((res) => {
      const data: ApiIfs<ProductCategoryIfs> = res.data;
      return data.body ?? updatedCategory;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.adminCategory.unknownError");
      console.error("Failed to update category:", message);
      alert(t("page.adminCategory.updateFailed", { message }));
      return originalCategory;
    });
};

const onEdit = async (
  updatedRow: ProductCategoryIfs,
  originalRow: ProductCategoryIfs,
  t: TFunction,
): Promise<ProductCategoryIfs> => {
  const { isNew, ...categoryData } = updatedRow;
  if (isNew) {
    return await createCategory(categoryData, originalRow, t);
  }
  return await updateCategory(updatedRow, originalRow, t);
};

const onDelete = async (id: number, t: TFunction): Promise<void> => {
  await privateApi
    .delete(`/v1/admin/categories/${id}`)
    .then(() => {
      console.log("Category deleted successfully");
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || t("page.adminCategory.unknownError");
      console.error("Failed to delete category:", message);
      alert(t("page.adminCategory.deleteFailed", { message }));
    });
};

function EditToolbar(props: GridSlotProps["toolbar"]) {
  const { setRows, setRowModesModel } = props;
  const { t } = useTranslation();

  const handleClick = () => {
    const id = randomId();
    setRows((oldRows) => [
      ...oldRows,
      { id, code: "", name: "", description: "", isNew: true },
    ]);
    setRowModesModel((oldModel) => ({
      ...oldModel,
      [id]: { mode: GridRowModes.Edit, fieldToFocus: "code" },
    }));
  };

  return (
    <Toolbar>
      <Typography sx={{ fontWeight: "medium", flex: 1, mx: 0.5 }}>
        {props.toolbarName}
      </Typography>
      <Tooltip title={t("page.adminCategory.addRecord")}>
        <ToolbarButton onClick={handleClick}>
          <AddIcon fontSize="small" />
        </ToolbarButton>
      </Tooltip>
    </Toolbar>
  );
}

export default function AdminCategory() {
  const { t } = useTranslation();
  const columns = createColumns(t);
  const [rows, setRows] = useState<ProductCategoryIfs[]>([]);
  const [rowModesModel, setRowModesModel] = useState<GridRowModesModel>({});
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState(
    dataGridInitialState.pagination.paginationModel,
  );

  const fetchCategories = useCallback(async (page: number, size: number) => {
    setLoading(true);

    await privateApi
      .get("/v1/admin/categories", { params: { page, size } })
      .then((res) => {
        const data: ApiIfs<PageIfs<ProductCategoryIfs>> = res.data;
        setRows(data?.body?.content ?? []);
        setRowCount(data?.body?.page?.totalElements ?? 0);
      })
      .catch((error) => {
        console.error("Error fetching categories:", error);
        alert(t("page.adminCategory.fetchFailed"));
      })
      .finally(() => {
        setLoading(false);
      });
  }, [t]);

  useEffect(() => {
    queueMicrotask(() => {
      void fetchCategories(paginationModel.page, paginationModel.pageSize);
    });
  }, [fetchCategories, paginationModel.page, paginationModel.pageSize]);

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
          fetchCategories(paginationModel.page, paginationModel.pageSize),
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
    [fetchCategories, paginationModel.page, paginationModel.pageSize, t],
  );

  const processRowUpdate = async (
    updatedRow: ProductCategoryIfs,
    originalRow: ProductCategoryIfs,
  ): Promise<ProductCategoryIfs> => {
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
      <Title>{t("page.adminCategory.title")}</Title>
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
                toolbarName: t("page.adminCategory.toolbarName"),
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
