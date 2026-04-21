import { Outlet, useNavigate } from "react-router-dom";
import styled from "styled-components";
import { useAuth } from "../shared/auth/auth-context";
import { privateApi } from "../shared/api";

const Wrapper = styled.div`
  display: grid;
  gap: 20px;
  grid-template-columns: 1fr 7fr;
  height: 100%;
  padding: 50px 0px;
  width: 100%;
  max-width: 100%;
`;

const Nav = styled.nav`
  display: flex;
  flex-direction: column;
  align-items: start;
  padding-left: 20px;
`;

const LogoutBtn = styled.button`
  border: none;
  background-color: transparent;
  cursor: pointer;
  font-size: 16px;
`;

export default function Layout() {
  const { user, setUser } = useAuth();
  const navigate = useNavigate();

  const onLogout = async () => {
    privateApi
      .get("/logout")
      .then(() => {
        setUser(null);
        navigate("/login");
      })
      .catch((error) => {
        console.error(error);
      });
  };

  return (
    <Wrapper>
      <Nav>
        {user?.username && <LogoutBtn onClick={onLogout}>Logout</LogoutBtn>}
      </Nav>
      <Outlet />
    </Wrapper>
  );
}
