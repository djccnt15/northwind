import { createGlobalStyle, css } from "styled-components";
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
