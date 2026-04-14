import styled from "styled-components";
import { CommBorderRadius } from "../shared/boder";

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-bottom: 200px;
`;

const H1 = styled.h1`
  margin: 20px 0px;
  font-size: 24px;
  font-weight: 600;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
`;

const Input = styled.input`
  ${CommBorderRadius}
  padding: 10px;
  width: 200px;
  border: 1px solid #ccc;
  font-size: 16px;
`;

const Button = styled.button`
  ${CommBorderRadius}
  width: 100%;
  padding: 10px 20px;
  border: none;
  background-color: #17c1ff;
  color: white;
  font-size: 16px;
  cursor: pointer;
  &:hover {
    background-color: #2397c9;
  }
`;

export default function Login() {
  return (
    <Wrapper>
      <H1>Please Sign In</H1>
      <Form>
        <Input type="text" placeholder="Username" />
        <Input type="password" placeholder="Password" />
        <Button type="submit">Login</Button>
      </Form>
    </Wrapper>
  );
}
