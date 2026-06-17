import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/DeleteOutlined";
import SaveIcon from "@mui/icons-material/Save";
import CancelIcon from "@mui/icons-material/Close";
import {
  type GridRenderCellParams,
  useGridApiContext,
  useGridSelector,
  gridEditRowsStateSelector,
  GridActionsCell,
  GridActionsCellItem,
} from "@mui/x-data-grid";
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import { ActionHandlersContext } from "./action-context";
import React from "react";

export default function ActionsCell(props: GridRenderCellParams) {
  const { t } = useTranslation();
  const apiRef = useGridApiContext();
  const rowModesModel = useGridSelector(apiRef, gridEditRowsStateSelector);
  const isInEditMode = typeof rowModesModel[props.id] !== "undefined";

  const {
    handleSaveClick,
    handleCancelClick,
    handleEditClick,
    handleDeleteClick,
  } = useContext(ActionHandlersContext);

  return (
    <GridActionsCell {...props}>
      {isInEditMode ? (
        <React.Fragment>
          <GridActionsCellItem
            icon={<SaveIcon />}
            label={t("dataGrid.save")}
            material={{ sx: { color: "primary.main" } }}
            onClick={() => handleSaveClick(props.id)}
          />
          <GridActionsCellItem
            icon={<CancelIcon />}
            label={t("dataGrid.cancel")}
            className="textPrimary"
            onClick={() => handleCancelClick(props.id)}
            color="inherit"
          />
        </React.Fragment>
      ) : (
        <React.Fragment>
          <GridActionsCellItem
            icon={<EditIcon />}
            label={t("dataGrid.edit")}
            className="textPrimary"
            onClick={() => handleEditClick(props.id)}
            color="inherit"
          />
          <GridActionsCellItem
            icon={<DeleteIcon />}
            label={t("dataGrid.delete")}
            onClick={() => handleDeleteClick(props.id)}
            color="inherit"
          />
        </React.Fragment>
      )}
    </GridActionsCell>
  );
}
