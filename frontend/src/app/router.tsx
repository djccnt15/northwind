import { createBrowserRouter } from "react-router-dom";
import Login from "../pages/login";

export const AppRouter = createBrowserRouter([
  {
    path: "/login",
    element: <Login />,
  },
]);
