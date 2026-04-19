import { createContext, useContext } from "react";
import type { UserIfs } from "../../entities/app/auth";

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
