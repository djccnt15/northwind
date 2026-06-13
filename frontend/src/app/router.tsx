import { createBrowserRouter } from "react-router-dom";
import Layout from "./layout";
import Login from "../pages/login";
import Home from "../pages/home";
import {
  AdminRoute,
  AuthRedirectRoute,
  CompanyRoute,
  ManagerRoute,
  NotFoundRoute,
  OrderRoute,
  ProductRoute,
  ProtectedRoute,
  PurchaseRoute,
  StockRoute,
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
import Companies from "../pages/companies";
import CompanyDetail from "../pages/company-detail";
import Orders from "../pages/orders";
import OrderNew from "../pages/order-new";
import OrderDetail from "../pages/order-detail";
import PurchaseOrders from "../pages/purchase-orders";
import PurchaseOrderNew from "../pages/purchase-order-new";
import PurchaseOrderDetail from "../pages/purchase-order-detail";
import StockTake from "../pages/stock-take";

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
    ],
  },
  {
    path: "/",
    element: (
      <CompanyRoute>
        <Layout />
      </CompanyRoute>
    ),
    children: [
      { path: "/companies", element: <Companies /> },
      { path: "/companies/:id", element: <CompanyDetail /> },
    ],
  },
  {
    path: "/",
    element: (
      <OrderRoute>
        <Layout />
      </OrderRoute>
    ),
    children: [
      { path: "/orders", element: <Orders /> },
      { path: "/orders/new", element: <OrderNew /> },
      { path: "/orders/:id", element: <OrderDetail /> },
    ],
  },
  {
    path: "/",
    element: (
      <PurchaseRoute>
        <Layout />
      </PurchaseRoute>
    ),
    children: [
      { path: "/purchase-orders", element: <PurchaseOrders /> },
      { path: "/purchase-orders/new", element: <PurchaseOrderNew /> },
      { path: "/purchase-orders/:id", element: <PurchaseOrderDetail /> },
    ],
  },
  {
    path: "/",
    element: (
      <ProductRoute>
        <Layout />
      </ProductRoute>
    ),
    children: [
      { path: "/products", element: <Products /> },
      { path: "/products/new", element: <ProductDetail /> },
      { path: "/products/:id", element: <ProductDetail /> },
    ],
  },
  {
    path: "/",
    element: (
      <StockRoute>
        <Layout />
      </StockRoute>
    ),
    children: [{ path: "/stock-take", element: <StockTake /> }],
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
    ],
  },
  {
    path: "/admin",
    element: (
      <ManagerRoute>
        <Layout />
      </ManagerRoute>
    ),
    children: [{ path: "/admin/categories", element: <AdminCategory /> }],
  },
  { path: "*", element: <NotFoundRoute /> },
]);
