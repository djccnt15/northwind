import "@mui/x-data-grid";

declare module "@mui/x-data-grid" {
  interface ToolbarPropsOverrides {
    toolbarName?: string;
    debounceMs?: number;
    defaultExpanded?: boolean;
    expanded?: boolean;
  }
}
