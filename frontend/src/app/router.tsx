import { createBrowserRouter } from "react-router-dom";
import Layout from "./layout";
import Login from "../pages/login";
import Home from "../pages/home";
import {
  AuthRedirectRoute,
  NotFoundRoute,
  ProtectedRoute,
} from "./provider/redirect-route";

export const AppRouter = createBrowserRouter([
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      {
        path: "/",
        element: <Home />,
      },
    ],
  },
  {
    path: "/login",
    element: (
      <AuthRedirectRoute>
        <Login />
      </AuthRedirectRoute>
    ),
  },
  {
    path: "*",
    element: <NotFoundRoute />,
  },
]);
