import { useEffect, useState } from "react";
import type { ChildNodeIfs, SessionIfs, ApiIfs } from "../../entities/app";
import { privateApi } from "../../shared/api";
import { AuthContext, responseToUser } from "../../features/auth";

export default function AuthProvider({ children }: ChildNodeIfs) {
  const [user, setUser] = useState<SessionIfs | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = () => {
      privateApi
        .get("/v1/auth/check-session")
        .then((res) => {
          const data: ApiIfs<SessionIfs> = res.data;
          console.log("Session check response:", data);
          if (data.result.code === 1200) {
            setUser(responseToUser(data));
          } else {
            setUser(null);
          }
        })
        .catch((err) => {
          const data: ApiIfs<null> = err.response?.data;
          console.error(data || err);
          setUser(null);
        })
        .finally(() => {
          setIsLoading(false);
        });
    };

    initializeAuth();
  }, []);

  return (
    <AuthContext value={{ user, setUser }}>
      {!isLoading && children}
    </AuthContext>
  );
}
