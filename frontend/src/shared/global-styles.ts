import styled, { createGlobalStyle, css } from "styled-components";
import reset from "styled-reset";

export const GlobalStyles = createGlobalStyle`
  ${reset};

  html,
  body,
  #root {
    width: 100%;
    height: 100%;
  }

  * {
    box-sizing: border-box;
  };

  body {
    min-height: 100dvh;
    background-color: white;
    color: black;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  };

  /* ::-webkit-scrollbar {
    display:none;
  } */
`;

export const commBorderRadius = css`
  border-radius: 4px;
`;

export const commBtnSkyBlue = css`
  background-color: #17c1ff;
`;

export const commBtnHoverSkyBlue = css`
  background-color: #2397c9;
`;

export const commBtnSkyBlueBoxShadow = css`
  box-shadow: 0 0 0 1px #006885;
`;

export const commLinkSkyBlue = css`
  color: #17c1ff;
`;

export const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9998;
`;

export const ModalFadeInAnimation = css`
  animation: fadeIn 0.5s ease-out forwards;

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
`;
