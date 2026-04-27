import { Link, Outlet, useNavigate } from "react-router-dom";
import styled from "styled-components";
import { useAuth } from "../shared/auth/auth-context";
import { privateApi } from "../shared/api";

const Wrapper = styled.div`
  display: grid;
  grid-template-columns: auto 1fr;
  width: 100%;
  height: 100%;
  min-height: 0;
  max-width: 100%;
`;

const ContentArea = styled.div`
  overflow-y: auto;
  height: 100%;
  min-height: 0;
  min-width: 0;
  padding: 20px;
`;

const Nav = styled.nav`
  display: flex;
  background-color: #efefef;
  flex-direction: column;
  align-items: start;
  min-width: 150px;
  padding: 20px 0;
`;

const NavItem = styled.div`
  font-size: 16px;
  padding: 10px 20px;
  width: 100%;
  cursor: pointer;

  a {
    color: black;
    text-decoration: none;
    display: flex;
    align-items: center;
  }

  svg {
    width: 20px;
    padding-right: 5px;
  }

  &:hover {
    background-color: #d4d4d4;
    transition: background-color 0.3s ease-in-out;
  }

  &.log-out {
    display: flex;
    align-items: center;
    font-weight: 550;
  }
`;

export default function Layout() {
  const { user, setUser } = useAuth();
  const navigate = useNavigate();

  const onLogout = async () => {
    privateApi
      .get("/v1/logout")
      .then(() => {
        setUser(null);
        navigate("/login");
      })
      .catch((err) => {
        console.error(err);
      });
  };

  return (
    <Wrapper>
      <Nav>
        <NavItem>
          <Link to="/">
            <svg
              fill="none"
              strokeWidth={1.5}
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="m2.25 12 8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25"
              />
            </svg>
            Home
          </Link>
        </NavItem>
        {user?.authorities.includes("admin") && (
          <NavItem>
            <Link to="/admin/user">
              <svg
                fill="none"
                strokeWidth={1.5}
                stroke="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M15 19.128a9.38 9.38 0 0 0 2.625.372 9.337 9.337 0 0 0 4.121-.952 4.125 4.125 0 0 0-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 0 1 8.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0 1 11.964-3.07M12 6.375a3.375 3.375 0 1 1-6.75 0 3.375 3.375 0 0 1 6.75 0Zm8.25 2.25a2.625 2.625 0 1 1-5.25 0 2.625 2.625 0 0 1 5.25 0Z"
                />
              </svg>
              사용자 관리
            </Link>
          </NavItem>
        )}
        {user?.loggedIn && (
          <NavItem className="log-out" onClick={onLogout}>
            <svg
              fill="none"
              strokeWidth={1.5}
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M15.75 9V5.25A2.25 2.25 0 0 0 13.5 3h-6a2.25 2.25 0 0 0-2.25 2.25v13.5A2.25 2.25 0 0 0 7.5 21h6a2.25 2.25 0 0 0 2.25-2.25V15m3 0 3-3m0 0-3-3m3 3H9"
              />
            </svg>
            Logout
          </NavItem>
        )}
      </Nav>
      <ContentArea>
        <Outlet />
      </ContentArea>
    </Wrapper>
  );
}
