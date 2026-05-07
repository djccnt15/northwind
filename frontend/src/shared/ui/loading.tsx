import styled from "styled-components";
import { Spinner } from "./spinner";

const LoadingOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
`;

interface LoadingScreenProps {
  size?: string;
  thickness?: string;
  color?: string;
}

export default function LoadingScreen({
  size,
  thickness,
  color,
}: LoadingScreenProps) {
  return (
    <LoadingOverlay>
      <Spinner size={size} thickness={thickness} color={color} />
    </LoadingOverlay>
  );
}
