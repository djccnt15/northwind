import styled from "styled-components";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  globalTransition,
  Title,
} from "../shared/ui/global-styles";
import { SubmitBtn } from "../shared/ui/auth-ui";
import { useAuth } from "../shared/auth/auth-context";
import { useState } from "react";
import { privateApi } from "../shared/api";
import type { ApiIfs } from "../entities/app/api";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
`;

const ContentWrapper = styled.div`
  padding-left: 20px;
  gap: 30px;
`;

const GapDiv = styled.div<{ padding?: string }>`
  padding: ${(props) => props.padding || "15px"};
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  width: 500px;
  gap: 20px;
`;

const FieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

const Label = styled.label`
  display: block;
  padding-bottom: 8px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
`;

const Input = styled.input`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 16px;
  width: 100%;
  height: 40px;
  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const PasswordWrapper = styled.div`
  display: flex;
  gap: 10px;
`;

const SubmitPwBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  height: 40px;
  width: 50px;
  border: none;
  color: white;
  font-size: 10px;
  margin-top: 20px;
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

const AuthList = styled.ul`
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  align-content: flex-start;
  width: 100%;
  height: min(230px, calc(100dvh - 280px));
  min-height: 120px;
  overflow-x: auto;
  overflow-y: hidden;
  gap: 10px;
  padding: 0 0 6px 0;
  list-style: none;
`;

const AuthItem = styled.li`
  padding: 4px 8px;
  background-color: #dddddd;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
`;

export default function Profile() {
  const { user } = useAuth();

  const [username, setUsername] = useState<string>(user?.username || "");
  const [email, setEmail] = useState<string>(user?.email || "");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isPasswordLoading, setIsPasswordLoading] = useState<boolean>(false);

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

  const onSubmitProfile = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isLoading) return;

    if (username === user.username && email === user.email) {
      alert("No changes detected.");
      return;
    }

    if (username.trim() === "" || email.trim() === "") {
      alert("Username and Email cannot be empty.");
      return;
    }

    setIsLoading(true);

    privateApi
      .patch(`/v1/user/${user.id}/profile`, { username, email })
      .then(() => {
        alert("Profile updated successfully.");
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message =
          description || "An error occurred while updating the profile.";
        alert(message);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const onSubmitPassword = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isPasswordLoading) return;

    if (password.trim() === "" || confirmPassword.trim() === "") {
      alert("Password and Confirm Password cannot be empty.");
      return;
    }

    if (password !== confirmPassword) {
      alert("Passwords do not match.");
      return;
    }

    setIsPasswordLoading(true);

    privateApi
      .patch(`/v1/user/${user.id}/password`, { password, confirmPassword })
      .then(() => {
        alert("Password updated successfully.");
        setPassword("");
        setConfirmPassword("");
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message =
          description || "An error occurred while updating the password.";
        alert(message);
      })
      .finally(() => {
        setIsPasswordLoading(false);
      });
  };

  return (
    <Wrapper>
      <Title>Profile</Title>
      <ContentWrapper>
        <Form onSubmit={onSubmitProfile}>
          <FieldWrapper>
            <Label>ID</Label>
            <Input
              type="text"
              value={username}
              onChange={onChangeUsername}
              required
            />
          </FieldWrapper>
          <FieldWrapper>
            <Label>E-mail</Label>
            <Input
              type="email"
              value={email}
              onChange={onChangeEmail}
              required
            />
          </FieldWrapper>
          <SubmitBtn value="Update Profile">Update Profile</SubmitBtn>
        </Form>
        <GapDiv />
        <Form onSubmit={onSubmitPassword}>
          <PasswordWrapper>
            <FieldWrapper>
              <Label>Password</Label>
              <Input
                type="password"
                placeholder="Password"
                value={password}
                onChange={onChangePassword}
                required
              />
            </FieldWrapper>
            <FieldWrapper>
              <Label>Confirm Password</Label>
              <Input
                type="password"
                placeholder="Password"
                value={confirmPassword}
                onChange={onChangeConfirmPassword}
                required
              />
            </FieldWrapper>
            <SubmitPwBtn>Change</SubmitPwBtn>
          </PasswordWrapper>
          <FieldWrapper>
            <Label>Live Until</Label>
            <Input type="datetime-local" value={user?.liveUntil} disabled />
          </FieldWrapper>
        </Form>
      </ContentWrapper>
      <GapDiv />
      <Title>Authorities</Title>
      <ContentWrapper>
        <AuthList>
          {user?.authorities.map((auth, index) => (
            <AuthItem key={index}>{auth}</AuthItem>
          ))}
        </AuthList>
      </ContentWrapper>
    </Wrapper>
  );
}
