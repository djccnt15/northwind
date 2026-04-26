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

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-bottom: 50px;
`;

export default function Signup() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAuth();

  const [errorMsg, setErrorMsg] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const [username, setUsername] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");

  const onChangeUsername = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const onChangeEmail = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  const onChangePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const onChangeConfirmPassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setConfirmPassword(e.target.value);
  };

  const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    setErrorMsg("");
    setIsLoading(true);

    api
      .post("/v1/signup", { username, email, password, confirmPassword })
      .then((response) => {
        const data = response.data;
        console.log("signup response:", data);
        setUser({
          id: data.id,
          username: data.username,
          authorities: Array.isArray(data.body.authorities)
            ? data.body.authorities.map(String)
            : [],
          loggedIn: true,
        });

        const from = location.state?.from?.pathname || "/";
        navigate(from, { replace: true });
      })
      .catch((error) => {
        console.error(error);
        setErrorMsg("Signup failed. Please contact the admin.");
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <Wrapper>
      <H1>Please Sign Up</H1>
      <Form onSubmit={onSubmit}>
        <Input
          type="text"
          placeholder="Username"
          value={username}
          onChange={onChangeUsername}
          required
        />
        <Input
          type="email"
          placeholder="Email"
          value={email}
          onChange={onChangeEmail}
          required
        />
        <Input
          type="password"
          placeholder="Password"
          value={password}
          onChange={onChangePassword}
          required
        />
        <Input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={onChangeConfirmPassword}
          required
        />
        <SubmitBtn
          type="submit"
          value={isLoading ? "Loading..." : "Sign Up"}
          disabled={isLoading}
        />
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        Already have an account? <Link to="/login">Log in &rarr;</Link>
      </Switcher>
    </Wrapper>
  );
}
