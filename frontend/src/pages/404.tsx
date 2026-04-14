import styled from "styled-components";

const Wrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 600;
  font-size: 24px;
  padding-bottom: 200px;
`;

export default function NotFound() {
  return <Wrapper>404 Not Found</Wrapper>;
}
