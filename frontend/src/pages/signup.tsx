import { useState } from "react";
import { Link } from "react-router-dom";
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
} from "../shared/ui/auth-ui";
import {
  commBorderRadius,
  commBtnSkyBlue,
  commBtnHoverSkyBlue,
  commBtnSkyBlueBoxShadow,
  ModalOverlay,
  commLinkSkyBlue,
  globalTransition,
  ModalDefault,
} from "../shared/ui/global-styles";
import type { ApiIfs } from "../entities/app/api";
import type { UserIfs } from "../entities/app/user";

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

const CheckEmailBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  height: 40px;
  border: none;
  color: white;
  font-size: 10px;
  padding: 10px;
  cursor: pointer;
  &:hover {
    ${commBtnHoverSkyBlue}
    ${globalTransition}
  }
  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const SuccessModal = styled(ModalDefault)`
  background-color: white;
  padding: 50px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  a {
    ${commLinkSkyBlue}
  }
`;

export default function Signup() {
  const [errorMsg, setErrorMsg] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isEmailChecked, setIsEmailChecked] = useState<boolean>(false);
  const [isSignupSuccess, setIsSignupSuccess] = useState<boolean>(false);

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
    if (isLoading) return;

    if (!email) {
      alert("Please enter an email.");
      return;
    }

    setIsLoading(true);

    api
      .get("/v1/check-email", { params: { email } })
      .then((res) => {
        console.log("check email response:", res.status);
        setIsEmailChecked(true);
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message = description
          ? `Failed to check email. ${description}`
          : "Failed to check email. Please try again.";
        alert(message);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (isLoading) return;

    if (username.trim() === "" || email.trim() === "") {
      alert("Username and Email cannot be empty.");
      return;
    }

    if (password !== confirmPassword) {
      alert("Password and confirm password do not match.");
      return;
    }

    setErrorMsg("");
    setIsLoading(true);

    api
      .post("/v1/signup", { username, email, password, confirmPassword })
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        console.log("signup response:", data);
        setIsSignupSuccess(true);
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message = description
          ? `Signup failed. ${description}`
          : "Signup failed. Please contact the admin.";
        setErrorMsg(message);
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
          placeholder="ID"
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
          <CheckEmailBtn type="button" onClick={onClickCheckEmail}>
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
          <SubmitBtn disabled={isSubmitDisabled}>
            {isLoading ? "Loading..." : "Sign Up"}
          </SubmitBtn>
          {isSubmitDisabled && (
            <SubmitBtnHoverMsg>{submitDisabledMessage}</SubmitBtnHoverMsg>
          )}
        </SubmitBtnWrapper>
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        Already have an account? <Link to="/login">Log in &rarr;</Link>
      </Switcher>
      {isSignupSuccess && (
        <ModalOverlay>
          <SuccessModal>
            Sign Up Success! Go to <Link to="/login">Log in &rarr;</Link>
          </SuccessModal>
        </ModalOverlay>
      )}
    </Wrapper>
  );
}
