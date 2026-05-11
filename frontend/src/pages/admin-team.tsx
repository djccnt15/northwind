import styled from "styled-components";
import { PageWrapper, Title } from "../shared/ui/global-styles";
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
import type { ApiIfs, PageIfs } from "../entities/app/api";
import type { TeamIfs } from "../entities/employee";
import ActionsCell from "../features/data-grid/crud-cell";
import { privateApi } from "../shared/api";
import { Box, Tooltip } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { useEffect, useMemo, useState } from "react";
import {
  ActionHandlersContext,
  type ActionHandlers,
} from "../features/data-grid/action-context";

const Wrapper = styled(PageWrapper)``;

const columns: GridColDef[] = [
  { field: "name", headerName: "Team Name", flex: 1, editable: true },
  {
    field: "actions",
    type: "actions",
    headerName: "Actions",
    width: 100,
    cellClassName: "actions",
    renderCell: (params) => <ActionsCell {...params} />,
  },
];

const createTeam = async (
  updatedTeam: TeamIfs,
  originalTeam: TeamIfs,
): Promise<TeamIfs> => {
  return await privateApi
    .post("/v1/admin/teams", { name: updatedTeam.name })
    .then((res) => {
      const data: ApiIfs<TeamIfs> = res.data;
      return data.body ?? updatedTeam;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to create team:", message);
      alert(`Failed to create team: ${message}`);
      return originalTeam;
    });
};

const updateTeam = async (
  updatedTeam: TeamIfs,
  originalTeam: TeamIfs,
): Promise<TeamIfs> => {
  return await privateApi
    .put(`/v1/admin/teams/${updatedTeam.id}`, {
      name: updatedTeam.name,
    })
    .then((res) => {
      const data: ApiIfs<TeamIfs> = res.data;
      return data.body ?? updatedTeam;
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to update team:", message);
      alert(`Failed to update team: ${message}`);
      return originalTeam;
    });
};

const onEdit = async (
  updatedRow: TeamIfs,
  originalRow: TeamIfs,
): Promise<TeamIfs> => {
  if (updatedRow.isNew) {
    const { isNew, ...teamData } = updatedRow;
    return await createTeam(teamData, originalRow);
  }

  return await updateTeam(updatedRow, originalRow);
};

const onDelete = async (id: number): Promise<void> => {
  await privateApi
    .delete(`/v1/admin/teams/${id}`)
    .then(() => {
      console.log("Team deleted successfully");
    })
    .catch((err) => {
      const data: ApiIfs<null> = err.response?.data;
      const message = data?.result?.description || "Unknown error";
      console.error("Failed to delete team:", message);
      alert(`Failed to delete team: ${message}`);
    });
};

function EditToolbar(props: GridSlotProps["toolbar"]) {
  const { setRows, setRowModesModel } = props;

  const handleClick = () => {
    const id = randomId();
    setRows((oldRows) => [...oldRows, { id, name: "", isNew: true }]);
    setRowModesModel((oldModel) => ({
      ...oldModel,
      [id]: { mode: GridRowModes.Edit, fieldToFocus: "name" },
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

export default function AdminTeam() {
  const [rows, setRows] = useState<TeamIfs[]>([]);
  const [rowModesModel, setRowModesModel] = useState<GridRowModesModel>({});

  useEffect(() => {
    const fetchTeams = () => {
      privateApi
        .get("/v1/admin/teams")
        .then((res) => {
          const data: ApiIfs<PageIfs<TeamIfs>> = res.data;
          setRows(data?.body?.content ?? []);
        })
        .catch((error) => {
          console.error("Error fetching teams:", error);
        })
        .finally(() => {});
    };

    fetchTeams();
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

  const processRowUpdate = async (
    updatedRow: TeamIfs,
    originalRow: TeamIfs,
  ): Promise<TeamIfs> => {
    const savedRow = await onEdit(updatedRow, originalRow);
    const normalizedRow = { ...savedRow, isNew: false };

    setRows((prevRows) =>
      prevRows.map((row) => (row.id === originalRow.id ? normalizedRow : row)),
    );

    if (savedRow.id !== originalRow.id) {
      setRowModesModel((prevRowModesModel) => {
        const { [originalRow.id]: _, ...rest } = prevRowModesModel;
        return rest;
      });
    }

    return normalizedRow;
  };

  return (
    <Wrapper>
      <Title>Admin - Team Management</Title>
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
            pageSizeOptions={[10, 20, 50, 100]}
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
