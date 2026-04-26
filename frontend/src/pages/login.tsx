import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import styled from "styled-components";
import { api } from "../shared/api";
import {
  ErrorMsg,
  Form,
  H1,
  Input,
  SubmitBtn,
  Switcher,
} from "../shared/auth-ui";
import { useAuth } from "../shared/auth/auth-context";
import type { ApiIfs } from "../entities/app/api";

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-bottom: 50px;
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
      .post("/v1/login", params, {
        headers: { "content-type": "application/x-www-form-urlencoded" },
      })
      .then((res) => {
        const data: ApiIfs = res.data;
        console.log("login response:", data);
        setUser({
          id: Number(data.body?.id) || 0,
          username: String(data.body?.username),
          authorities: Array.isArray(data.body?.authorities)
            ? data.body.authorities.map(String)
            : [],
          loggedIn: true,
        });

        const from = location.state?.from?.pathname || "/";
        navigate(from, { replace: true });
      })
      .catch((err) => {
        const data: ApiIfs = err.response?.data;
        console.error(data || err);
        const description = data?.result?.description;
        const message = description
          ? `Login failed. ${description}`
          : "Login failed. Please check your username and password.";
        setErrorMsg(message);
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
          required
        />
        <Input
          type="password"
          placeholder="Password"
          value={password}
          onChange={onChangePassword}
          required
        />
        <SubmitBtn
          type="submit"
          value={isLoading ? "Loading..." : "Login"}
          disabled={isLoading}
        />
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        Don't have an account? <Link to="/signup">Create one &rarr;</Link>
      </Switcher>
    </Wrapper>
  );
}
