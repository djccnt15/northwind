import { createBrowserRouter } from "react-router-dom";
import Layout from "./layout";
import Login from "../pages/login";
import Home from "../pages/home";
import {
  AdminRoute,
  AuthRedirectRoute,
  NotFoundRoute,
  ProtectedRoute,
} from "./provider/redirect-route";
import Signup from "../pages/signup";
import AdminUser from "../pages/admin-user";

export const AppRouter = createBrowserRouter([
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [{ path: "/", element: <Home /> }],
  },
  {
    path: "/admin",
    element: (
      <AdminRoute>
        <Layout />
      </AdminRoute>
    ),
    children: [{ path: "/admin/user", element: <AdminUser /> }],
  },
  {
    path: "",
    element: <AuthRedirectRoute />,
    children: [
      { path: "/login", element: <Login /> },
      { path: "/signup", element: <Signup /> },
    ],
  },
  {
    path: "*",
    element: <NotFoundRoute />,
  },
]);
