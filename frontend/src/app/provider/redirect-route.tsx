import { Navigate, Outlet, useLocation } from "react-router-dom";
import type { ChildNodeIfs } from "../../entities/app";
import { useAuth } from "../../features/auth";

export function ProtectedRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    // 로그인 페이지로 리다이렉트, 원래 가려던 페이지 정보 전달
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

export function AdminRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();

  if (!user || !user.authorities.includes("ADMIN")) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
}

export function ProductRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();

  if (
    !user ||
    !(
      user.authorities.includes("ADMIN") ||
      user.authorities.includes("PRODUCT")
    )
  ) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
}

export function OrderRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();

  if (
    !user ||
    !(user.authorities.includes("ADMIN") || user.authorities.includes("ORDER"))
  ) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
}

export function PurchaseRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();

  if (
    !user ||
    !(
      user.authorities.includes("ADMIN") ||
      user.authorities.includes("PURCHASE")
    )
  ) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
}

export function CompanyRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();

  if (
    !user ||
    !(
      user.authorities.includes("ADMIN") ||
      user.authorities.includes("COMPANY")
    )
  ) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
}

export function ManagerRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();

  if (
    !user ||
    !(
      user.authorities.includes("ADMIN") ||
      user.authorities.includes("MANAGER")
    )
  ) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
}

export function AuthRedirectRoute() {
  // 이미 로그인한 사용자 접근시 홈 페이지로 리다이렉트
  const { user } = useAuth();
  if (user) return <Navigate to="/home" replace />;

  return <Outlet />;
}

export function NotFoundRoute() {
  return <Navigate to="/home" replace />;
}
