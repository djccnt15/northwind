import styled from "styled-components";
import { commBorderRadius } from "./global-styles";

export const H1 = styled.h1`
  margin: 20px 0px;
  font-size: 24px;
  font-weight: 600;
`;

export const Form = styled.form`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  width: 250px;
  margin-bottom: 20px;
`;

export const Input = styled.input`
  ${commBorderRadius}
  padding: 10px;
  width: 100%;
  border: 1px solid #ccc;
  font-size: 16px;
  height: 40px;
`;

export const SubmitBtn = styled.input`
  ${commBorderRadius}
  width: 100%;
  height: 40px;
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

export const ErrorMsg = styled.span`
  color: tomato;
  font-size: 16px;
  font-weight: 600;
`;

export const Switcher = styled.span`
  margin-top: 20px;
  a {
    color: skyblue;
  }
`;
