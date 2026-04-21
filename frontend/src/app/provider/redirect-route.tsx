import { Navigate, useLocation } from "react-router-dom";
import type { ChildNodeIfs } from "../../entities/app/app";
import { useAuth } from "../../shared/auth/auth-context";

export function ProtectedRoute({ children }: ChildNodeIfs) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    // 로그인 페이지로 리다이렉트, 원래 가려던 페이지 정보 전달
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

export function AuthRedirectRoute({ children }: ChildNodeIfs) {
  // 이미 로그인한 사용자 접근시 홈 페이지로 리다이렉트
  const { user } = useAuth();
  if (user) return <Navigate to="/" replace />;

  return <>{children}</>;
}

export function NotFoundRoute() {
  return <Navigate to="/" replace />;
}
