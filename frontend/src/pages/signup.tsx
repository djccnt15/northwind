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
import {
  commBorderRadius,
  commBtnSkyBlue,
  commBtnHoverSkyBlue,
  commBtnSkyBlueBoxShadow,
} from "../shared/global-styles";
import type { ApiIfs } from "../entities/app/api";

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-bottom: 50px;
`;

const EmailCheckWrapper = styled.div`
  display: grid;
  grid-template-columns: 5fr 1fr;
  width: 100%;
  gap: 10px;
`;

const CheckEmailBtn = styled.div`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  height: 40px;
  border: none;
  color: white;
  font-size: 10px;
  padding: 10px;
  cursor: pointer;
  &:hover {
    ${commBtnHoverSkyBlue}
  }
  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

export default function Signup() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAuth();

  const [errorMsg, setErrorMsg] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isEmailChecked, setIsEmailChecked] = useState<boolean>(false);

  const [username, setUsername] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");

  const onChangeUsername = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const onChangeEmail = (e: React.ChangeEvent<HTMLInputElement>) => {
    setIsEmailChecked(false);
    setEmail(e.target.value);
  };

  const onChangePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const onChangeConfirmPassword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setConfirmPassword(e.target.value);
  };

  const onClickCheckEmail = () => {
    if (!email) {
      alert("Please enter an email.");
      return;
    }

    api
      .get("/v1/check-email", { params: { email } })
      .then((res) => {
        console.log("check email response:", res.status);
        setIsEmailChecked(true);
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs = err.response?.data;
        const description = data?.result?.description;
        const message = description
          ? `Failed to check email. ${description}`
          : "Failed to check email. Please try again.";
        alert(message);
      });
  };

  const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    setErrorMsg("");
    setIsLoading(true);

    api
      .post("/v1/signup", { username, email, password, confirmPassword })
      .then((res) => {
        const data = res.data;
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
      .catch((err) => {
        console.error(err);
        setErrorMsg("Signup failed. Please contact the admin.");
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const isSubmitDisabled = isLoading || !isEmailChecked;
  const submitDisabledMessage = isLoading
    ? "Signing up..."
    : !isEmailChecked
      ? "Please check your email before signing up."
      : "";

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
        <EmailCheckWrapper>
          <Input
            type="email"
            placeholder="Email"
            value={email}
            onChange={onChangeEmail}
            required
          />
          <CheckEmailBtn
            onClick={onClickCheckEmail}
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") onClickCheckEmail();
            }}
          >
            Check Email
          </CheckEmailBtn>
        </EmailCheckWrapper>
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
        <SubmitBtnWrapper>
          <SubmitBtn
            type="submit"
            value={isLoading ? "Loading..." : "Sign Up"}
            disabled={isSubmitDisabled}
          />
          {isSubmitDisabled && (
            <SubmitBtnHoverMsg>{submitDisabledMessage}</SubmitBtnHoverMsg>
          )}
        </SubmitBtnWrapper>
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        Already have an account? <Link to="/login">Log in &rarr;</Link>
      </Switcher>
    </Wrapper>
  );
}
