import { createGlobalStyle, css } from "styled-components";
import reset from "styled-reset";

export const GlobalStyles = createGlobalStyle`
  ${reset};

  * {
    box-sizing: border-box;
  };

  body {
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
