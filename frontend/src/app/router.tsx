import { createBrowserRouter } from "react-router-dom";
import Layout from "./layout";
import Login from "../pages/login";
import Home from "../pages/home";
import {
  AuthRedirectRoute,
  NotFoundRoute,
  ProtectedRoute,
} from "./provider/redirect-route";
import Signup from "../pages/signup";

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
    path: "",
    element: <AuthRedirectRoute />,
    children: [
      {
        path: "/login",
        element: <Login />,
      },
      {
        path: "/signup",
        element: <Signup />,
      },
    ],
  },
  {
    path: "*",
    element: <NotFoundRoute />,
  },
]);
