import styled from "styled-components";
import { useAuth } from "../features/auth";

const Wrapper = styled.div``;

const H1 = styled.h1``;

const P = styled.p``;

export default function Home() {
  const { user } = useAuth();

  return (
    <Wrapper>
      <H1>Welcome to Northwind! {user?.username}</H1>
      <P>
        This is the home page. Please use the navigation menu to explore the
        app.
      </P>
    </Wrapper>
  );
}
