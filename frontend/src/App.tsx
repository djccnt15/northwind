import styled, { createGlobalStyle } from "styled-components";
import reset from "styled-reset";
import { RouterProvider } from "react-router-dom";
import { AppRouter } from "./app/router";

const Wrapper = styled.div`
  height: 100vh;
  display: flex;
  justify-content: center;
`;

const GlobalStyles = createGlobalStyle`
  ${reset};

  * {
    box-sizing: border-box;
  };

  body {
    background-color: white;
    color: black;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  };

  ::-webkit-scrollbar {
    display:none;
  }
`;

function App() {
  return (
    <Wrapper>
      <GlobalStyles />
      <RouterProvider router={AppRouter} />
    </Wrapper>
  );
}

export default App;
