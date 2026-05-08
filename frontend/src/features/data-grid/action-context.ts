import type { GridRowId } from "@mui/x-data-grid/models";
import { createContext } from "react";

export interface ActionHandlers {
  handleCancelClick: (id: GridRowId) => void;
  handleDeleteClick: (id: GridRowId) => void;
  handleEditClick: (id: GridRowId) => void;
  handleSaveClick: (id: GridRowId) => void;
}

export const ActionHandlersContext = createContext<ActionHandlers>({
  handleCancelClick: () => {},
  handleDeleteClick: () => {},
  handleEditClick: () => {},
  handleSaveClick: () => {},
});
