import { createContext, useContext } from "react";
import type { SessionIfs, ApiIfs } from "../../entities/app";

interface AuthContextIfs {
  user: SessionIfs | null;
  setUser: React.Dispatch<React.SetStateAction<SessionIfs | null>>;
}

export const AuthContext = createContext<AuthContextIfs | null>(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
};

export const responseToUser = (data: ApiIfs<SessionIfs>): SessionIfs => {
  return {
    id: Number(data.body?.id) || 0,
    username: String(data.body?.username) || "",
    authorities: Array.isArray(data.body?.authorities)
      ? data.body.authorities.map(String)
      : [],
    loggedIn: true,
  };
};
