import { useEffect, useState } from "react";
import type { ChildNodeIfs } from "../../entities/app/app";
import type { UserIfs } from "../../entities/app/auth";
import { privateApi } from "../../shared/api";
import { AuthContext } from "../../shared/auth/auth";

export default function AuthProvider({ children }: ChildNodeIfs) {
  const [user, setUser] = useState<UserIfs | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = () => {
      privateApi
        .get("/auth/check-session")
        .then((response) => {
          const data = response.data;
          console.log("Session check response:", data);
          setUser({ id: data.id, username: data.username, loggedIn: true });
        })
        .catch((err) => {
          console.error(err);
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
