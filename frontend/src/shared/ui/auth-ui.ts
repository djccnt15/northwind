import styled from "styled-components";
import {
  commBorderRadius,
  commBtnSkyBlue,
  commBtnHoverSkyBlue,
  commBtnSkyBlueBoxShadow,
  commLinkSkyBlue,
  globalTransition,
  TooltipWrapper,
  Tooltip,
} from "./global-styles";

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
  width: 300px;
  margin-bottom: 20px;
`;

export const Input = styled.input`
  ${commBorderRadius}
  padding: 10px;
  width: 100%;
  border: 1px solid #ccc;
  font-size: 16px;
  height: 40px;
  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

export const SubmitBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  width: 100%;
  display: block;
  height: 40px;
  padding: 10px 20px;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  &:hover {
    ${commBtnHoverSkyBlue}
    ${globalTransition}
  }
  &:disabled {
    background-color: #9eb6c2;
    cursor: default;
    pointer-events: none; // 마우스 이벤트를 받지 않도록 설정
  }
  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

export const SubmitBtnWrapper = styled(TooltipWrapper)`
  width: 100%;
`;

export const SubmitBtnHoverMsg = styled(Tooltip)`
  // 버튼의 가로 중앙에 위치하도록 설정(툴팁의 왼쪽 끝을 부모의 정중앙선에 맞춤)
  left: 50%;

  // 툴팁의 가로 중앙이 버튼의 가로 중앙과 일치하도록 이동(툴팁 자신의 너비의 절반(50%)만큼 왼쪽으로 이동)
  transform: translateX(-50%);

  // 버튼의 바로 위에 위치하도록 설정(버튼의 높이(100%) + 버튼과 툴팁 사이의 간격(8px))
  bottom: calc(100% + 8px);
`;

export const ErrorMsg = styled.span`
  color: tomato;
  font-size: 16px;
  font-weight: 600;
`;

export const Switcher = styled.span`
  margin-top: 20px;
  a {
    ${commLinkSkyBlue}
  }
`;
