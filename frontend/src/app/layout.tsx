import { Outlet } from "react-router-dom";
import styled from "styled-components";
import { LeftNavBar } from "../widgets";

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

export default function Layout() {
  return (
    <Wrapper>
      <LeftNavBar />
      <ContentArea>
        <Outlet />
      </ContentArea>
    </Wrapper>
  );
}
