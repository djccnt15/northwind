import { useNavigate } from "react-router-dom";
import { api, privateApi } from "../../shared/api";
import { useAuth } from "./auth-context";

export const useLogout = () => {
  const { setUser } = useAuth();
  const navigate = useNavigate();

  return () => {
    privateApi
      .post("/v1/logout")
      .then(async () => {
        setUser(null);
        navigate("/login");
        await api.get("/v1/auth/csrf-token");
      })
      .catch((err) => {
        console.error(err);
      });
  };
};
