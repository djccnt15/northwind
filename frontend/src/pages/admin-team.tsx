import styled from "styled-components";
import { PageWrapper, Title } from "../shared/ui/global-styles";

const Wrapper = styled(PageWrapper)``;

export default function AdminTeam() {
  return (
    <Wrapper>
      <Title>Admin - Team Management</Title>
      <p>This page is accessible only to users with ADMIN role.</p>
    </Wrapper>
  );
}
