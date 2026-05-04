import styled, { createGlobalStyle, css } from "styled-components";
import reset from "styled-reset";

export const GlobalStyles = createGlobalStyle`
  ${reset};

  html, body, #root {
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

export const globalTransition = css`
  transition: background-color 0.2s ease-in-out;
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

const modalFadeInAnimation = css`
  animation: fadeIn 0.2s ease-out forwards;

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
`;

export const ModalOverlay = styled.div`
  ${modalFadeInAnimation}
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

export const ModalDefault = styled.div`
  ${commBorderRadius}
  z-index: 9999;
`;

export const Title = styled.h1`
  font-size: 24px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 20px;
  margin-bottom: 10px;
`;
