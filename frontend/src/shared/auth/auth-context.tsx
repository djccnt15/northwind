import { createContext, useContext } from "react";
import type { UserIfs } from "../../entities/app/user";
import type { ApiIfs } from "../../entities/app/api";

interface AuthContextIfs {
  user: UserIfs | null;
  setUser: React.Dispatch<React.SetStateAction<UserIfs | null>>;
}

export const AuthContext = createContext<AuthContextIfs | null>(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
};

export const responseToUser = (data: ApiIfs<UserIfs>): UserIfs => {
  return {
    id: Number(data.body?.id) || 0,
    username: String(data.body?.username) || "",
    email: String(data.body?.email) || "",
    authorities: Array.isArray(data.body?.authorities)
      ? data.body.authorities.map(String)
      : [],
    enabled: !!data.body?.enabled,
    liveUntil: String(data.body?.liveUntil) || "",
    passwordChangedAt: String(data.body?.passwordChangedAt) || "",
    loginFailedCount: Number(data.body?.loginFailedCount) || 0,
    lastLoginAt: String(data.body?.lastLoginAt) || "",
    team: String(data.body?.team) || "",
    loggedIn: true,
  };
};
