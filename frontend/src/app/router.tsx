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
import Profile from "../pages/profile";
import EmployeeTitle from "../pages/title";
import Welcome from "../pages/welcome";
import AdminTeam from "../pages/admin-team";
import AdminCategory from "../pages/admin-category";
import Products from "../pages/products";
import ProductDetail from "../pages/product-detail";

export const AppRouter = createBrowserRouter([
  {
    path: "/",
    element: <AuthRedirectRoute />,
    children: [
      { path: "/", element: <Welcome /> },
      { path: "/login", element: <Login /> },
      { path: "/signup", element: <Signup /> },
    ],
  },
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      { path: "/home", element: <Home /> },
      { path: "/profile", element: <Profile /> },
      { path: "/products", element: <Products /> },
      { path: "/products/:id", element: <ProductDetail /> },
    ],
  },
  {
    path: "/admin",
    element: (
      <AdminRoute>
        <Layout />
      </AdminRoute>
    ),
    children: [
      { path: "/admin/user", element: <AdminUser /> },
      { path: "/admin/titles", element: <EmployeeTitle /> },
      { path: "/admin/team", element: <AdminTeam /> },
      { path: "/admin/categories", element: <AdminCategory /> },
    ],
  },
  { path: "*", element: <NotFoundRoute /> },
]);
