import "@mui/x-data-grid";

declare module "@mui/x-data-grid" {
  interface ToolbarPropsOverrides {
    debounceMs?: number;
    defaultExpanded?: boolean;
    expanded?: boolean;
  }
}
