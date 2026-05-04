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
  SubmitBtnHoverMsg,
  SubmitBtnWrapper,
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

const CheckBoxArea = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
`;

const CheckBoxWrapper = styled.div`
  display: flex;
  align-items: center;
`;

const CheckBoxInput = styled.input`
  margin-right: 5px;
`;

const Label = styled.label`
  font-size: 14px;
`;

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAuth();

  const [errorMsg, setErrorMsg] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const [username, setUsername] = useState<string>(() => {
    return localStorage.getItem("rememberedId") || "";
  });
  const [password, setPassword] = useState<string>("");
  const [rememberMe, setRememberMe] = useState<boolean>(false);
  const [rememberId, setRememberId] = useState<boolean>(() => {
    return !!localStorage.getItem("rememberedId");
  });

  const onChangeUsername = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const onChangePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const onChangeRememberMe = (e: React.ChangeEvent<HTMLInputElement>) => {
    setRememberMe(e.target.checked);
  };

  const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (isLoading) return;

    setErrorMsg("");
    setIsLoading(true);

    const params = new URLSearchParams();
    params.append("username", username);
    params.append("password", password);
    params.append("remember-me", String(rememberMe));

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
          email: String(data.body?.email),
          authorities: Array.isArray(data.body?.authorities)
            ? data.body.authorities.map(String)
            : [],
          enabled: !!data.body?.enabled,
          liveUntil: String(data.body?.liveUntil),
          passwordChangedAt: String(data.body?.passwordChangedAt),
          loggedIn: true,
        });

        if (rememberId) {
          localStorage.setItem("rememberedId", username);
        } else {
          localStorage.removeItem("rememberedId");
        }

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
          placeholder="ID"
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
        <SubmitBtnWrapper>
          <SubmitBtn disabled={isLoading}>
            {isLoading ? "Loading..." : "Login"}
          </SubmitBtn>
          {isLoading && <SubmitBtnHoverMsg>Signing in...</SubmitBtnHoverMsg>}
        </SubmitBtnWrapper>
        <CheckBoxArea>
          <CheckBoxWrapper>
            <CheckBoxInput
              type="checkbox"
              id="rememberMe"
              checked={rememberMe}
              onChange={onChangeRememberMe}
            />
            <Label htmlFor="rememberMe">Remember Me</Label>
          </CheckBoxWrapper>
          <CheckBoxWrapper>
            <CheckBoxInput
              type="checkbox"
              id="rememberId"
              checked={rememberId}
              onChange={(e) => setRememberId(e.target.checked)}
            />
            <Label htmlFor="rememberId">Remember ID</Label>
          </CheckBoxWrapper>
        </CheckBoxArea>
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        Don't have an account? <Link to="/signup">Create one &rarr;</Link>
      </Switcher>
    </Wrapper>
  );
}
