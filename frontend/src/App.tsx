import styled from "styled-components";
import { RouterProvider } from "react-router-dom";
import { AppRouter } from "./app/router";
import AuthProvider from "./app/provider/auth-provider";
import { GlobalStyles } from "./shared/ui/global-styles";

const Wrapper = styled.div`
  height: 100vh;
  display: flex;
  justify-content: center;
`;

function App() {
  return (
    <Wrapper>
      <GlobalStyles />
      <AuthProvider>
        <RouterProvider router={AppRouter} />
      </AuthProvider>
    </Wrapper>
  );
}

export default App;
