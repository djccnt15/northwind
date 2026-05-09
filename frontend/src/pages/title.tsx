import AddIcon from "@mui/icons-material/Add";
import { Box, Tooltip } from "@mui/material";
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
import { useEffect, useMemo, useState } from "react";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app/api";
import type { TitleIfs } from "../entities/employee";
import {
  ActionHandlersContext,
  type ActionHandlers,
} from "../features/data-grid/action-context";
import ActionsCell from "../features/data-grid/crud-cell";
import { privateApi } from "../shared/api";
import { PageWrapper, Title } from "../shared/ui/global-styles";

const Wrapper = styled(PageWrapper)``;

const columns: GridColDef[] = [
  { field: "title", headerName: "Title Name", flex: 1, editable: true },
  {
    field: "actions",
    type: "actions",
    headerName: "Actions",
    width: 100,
    cellClassName: "actions",
    renderCell: (params) => <ActionsCell {...params} />,
  },
];

const createTitle = async (
  updatedTitle: TitleIfs,
  originalTitle: TitleIfs,
): Promise<TitleIfs> => {
  return await privateApi
    .post("/v1/employee/titles", { title: updatedTitle.title })
    .then((res) => {
      const data: ApiIfs<TitleIfs> = res.data;
      return data.body ?? updatedTitle;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to create title:", message);
      alert(`Failed to create title: ${message}`);
      return originalTitle;
    });
};

const updateTitle = async (
  updatedTitle: TitleIfs,
  originalTitle: TitleIfs,
): Promise<TitleIfs> => {
  return await privateApi
    .put(`/v1/employee/titles/${updatedTitle.id}`, {
      title: updatedTitle.title,
    })
    .then((res) => {
      const data: ApiIfs<TitleIfs> = res.data;
      return data.body ?? updatedTitle;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to update title:", message);
      alert(`Failed to update title: ${message}`);
      return originalTitle;
    });
};

const onEdit = async (
  updatedRow: TitleIfs,
  originalRow: TitleIfs,
): Promise<TitleIfs> => {
  console.log(originalRow);

  if (updatedRow.isNew) {
    const { isNew, ...titleData } = updatedRow;
    return await createTitle(titleData, originalRow);
  } else {
    return await updateTitle(updatedRow, originalRow);
  }
};

const onDelete = async (id: number): Promise<void> => {
  await privateApi
    .delete(`/v1/employee/titles/${id}`)
    .then(() => {
      console.log("Title deleted successfully");
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to delete title:", message);
      alert(`Failed to delete title: ${message}`);
    });
};

function EditToolbar(props: GridSlotProps["toolbar"]) {
  const { setRows, setRowModesModel } = props;

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
      <Tooltip title="Add record">
        <ToolbarButton onClick={handleClick}>
          <AddIcon fontSize="small" />
        </ToolbarButton>
      </Tooltip>
    </Toolbar>
  );
}

export default function EmployeeTitle() {
  const [rows, setRows] = useState<TitleIfs[]>([]);
  const [rowModesModel, setRowModesModel] = useState<GridRowModesModel>({});

  useEffect(() => {
    const fetchTitles = () => {
      privateApi
        .get("/v1/employee/titles/all")
        .then((res) => {
          const data: ApiIfs<TitleIfs[]> = res.data;
          setRows(data?.body || []);
        })
        .catch((error) => {
          console.error("Error fetching employee titles:", error);
        })
        .finally(() => {});
    };

    fetchTitles();
  }, []);

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
        onDelete(Number(id));
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
    [],
  );

  // const processRowUpdate = (newRow: GridRowModel) => {
  //   const updatedRow = { ...newRow, isNew: false };
  //   setRows((prevRows) =>
  //     prevRows.map((row) => (row.id === newRow.id ? updatedRow : row)),
  //   );
  //   return updatedRow;
  // };

  const processRowUpdate = async (
    updatedRow: TitleIfs,
    originalRow: TitleIfs,
  ): Promise<TitleIfs> => {
    return await onEdit(updatedRow, originalRow);
  };

  return (
    <Wrapper>
      <Title>Employee Title</Title>
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
            onRowModesModelChange={setRowModesModel}
            onRowEditStop={handleRowEditStop}
            processRowUpdate={processRowUpdate}
            showToolbar
            slots={{ toolbar: EditToolbar as GridSlots["toolbar"] }}
            slotProps={{
              toolbar: { setRows, setRowModesModel },
            }}
          />
        </ActionHandlersContext>
      </Box>
    </Wrapper>
  );
}
