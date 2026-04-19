import styled from "styled-components";
import { CommBorderRadius } from "../shared/boder";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../shared/auth/auth";
import { api } from "../shared/api";

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-bottom: 50px;
`;

const H1 = styled.h1`
  margin: 20px 0px;
  font-size: 24px;
  font-weight: 600;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  width: 250px;
  margin-bottom: 20px;
`;

const Input = styled.input`
  ${CommBorderRadius}
  padding: 10px;
  width: 100%;
  border: 1px solid #ccc;
  font-size: 16px;
  height: 40px;
`;

const SubmitBtn = styled.input`
  ${CommBorderRadius}
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

const ErrorMsg = styled.span`
  color: tomato;
  font-size: 16px;
  font-weight: 600;
`;

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAuth();

  const [errorMsg, setErrorMsg] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const onChangeUsername = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const onChangePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    setErrorMsg("");
    setIsLoading(true);

    const params = new URLSearchParams();
    params.append("username", username);
    params.append("password", password);

    api
      .post("/login", params, {
        headers: { "content-type": "application/x-www-form-urlencoded" },
      })
      .then((response) => {
        const data = response.data;
        console.log("login response:", data);
        setUser({ id: data.id, username: data.username, loggedIn: true });

        const from = location.state?.from?.pathname || "/";
        navigate(from, { replace: true });
      })
      .catch((error) => {
        console.error(error);
        setErrorMsg("Login failed. Please check your username and password.");
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <Wrapper>
      <H1>Please Sign In</H1>
      <Form onSubmit={onSubmit}>
        <Input
          type="text"
          placeholder="Username"
          value={username}
          onChange={onChangeUsername}
        />
        <Input
          type="password"
          placeholder="Password"
          value={password}
          onChange={onChangePassword}
        />
        <SubmitBtn
          type="submit"
          value={isLoading ? "Loading..." : "Login"}
          disabled={isLoading}
        />
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
    </Wrapper>
  );
}
