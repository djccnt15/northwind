import { Outlet } from "react-router-dom";
import styled from "styled-components";

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

export default function Layout() {
  return (
    <Wrapper>
      <Nav></Nav>
      <Outlet />
    </Wrapper>
  );
}
