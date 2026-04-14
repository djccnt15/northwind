import { createBrowserRouter } from "react-router-dom";
import Login from "../pages/login";
import NotFound from "../pages/404";

export const AppRouter = createBrowserRouter([
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "*",
    element: <NotFound />,
  },
]);
