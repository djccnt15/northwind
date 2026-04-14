import styled, { keyframes } from "styled-components";

const rotate = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
`;

export const Spinner = styled.div<{
  size?: string;
  thickness?: string;
  color?: string;
}>`
  width: ${(props) => props.size || "40px"};
  height: ${(props) => props.size || "40px"};
  border: ${(props) => props.thickness || "4px"} solid
    rgba(178, 178, 178, 0.123);
  border-top: ${(props) => props.thickness || "4px"} solid
    ${(props) => props.color || "#3498db"};
  border-radius: 50%;
  animation: ${rotate} 1.5s linear infinite;
  margin: 20px auto;
`;
