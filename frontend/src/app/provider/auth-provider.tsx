import { useEffect, useState } from "react";
import type { ChildNodeIfs } from "../../entities/app/app";
import type { UserIfs } from "../../entities/app/user";
import { privateApi } from "../../shared/api";
import { AuthContext } from "../../shared/auth/auth-context";
import type { ApiIfs } from "../../entities/app/api";

export default function AuthProvider({ children }: ChildNodeIfs) {
  const [user, setUser] = useState<UserIfs | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = () => {
      privateApi
        .get("/v1/auth/check-session")
        .then((res) => {
          const data: ApiIfs = res.data;
          console.log("Session check response:", data);
          setUser({
            id: Number(data.body?.id) || 0,
            username: String(data.body?.username),
            email: String(data.body?.email),
            authorities: Array.isArray(data.body?.authorities)
              ? data.body.authorities?.map(String)
              : [],
            loggedIn: true,
          });
        })
        .catch((err) => {
          const data: ApiIfs = err.response?.data;
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
