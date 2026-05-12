import styled from "styled-components";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  globalTransition,
  Title,
  Tooltip,
  TooltipWrapper,
} from "../shared/ui/global-styles";
import { SubmitBtn } from "../shared/ui/auth-ui";
import { useAuth } from "../shared/auth/auth-context";
import { useEffect, useState } from "react";
import { privateApi } from "../shared/api";
import type { ApiIfs } from "../entities/app/api";
import type { UserIfs } from "../entities/app/user";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  min-width: 1200px;
  display: flex;
`;

const PageWrapper = styled.div`
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto 1fr;
  overflow-y: auto;
`;

const ContentWrapper = styled.div`
  padding-left: 20px;
  height: 100%;
  overflow-y: auto;
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
  flex-wrap: wrap;
  width: 100%;
  gap: 10px;
  list-style: none;
`;

const AuthItem = styled.li`
  padding: 4px 8px;
  background-color: #dddddd;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  cursor: default;

  &:hover {
    background-color: #cccccc;
  }
`;

export default function Profile() {
  const { user, setUser } = useAuth();

  const [username, setUsername] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [userInfo, setUserInfo] = useState<UserIfs | null>(null);

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

  useEffect(() => {
    const fetchUserInfo = async () => {
      privateApi
        .get(`/v1/user/${user?.id}`)
        .then((res) => {
          const data: ApiIfs<UserIfs> = res.data;
          setUserInfo(data.body || null);
          setUsername(data.body?.username || "");
          setEmail(data.body?.email || "");
        })
        .catch((err) => {
          console.error("Failed to fetch user info:", err);
          alert("Failed to fetch user info. Please try again later.");
        });
    };

    fetchUserInfo();
  }, [user?.id]);

  const onSubmitProfile = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isLoading) return;

    if (username === userInfo?.username && email === userInfo?.email) {
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
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        setUserInfo(data.body || null);
        setUser({ ...user, username: data.body?.username || user.username });
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
      <PageWrapper>
        <Title>Profile</Title>
        <ContentWrapper>
          <Form onSubmit={onSubmitProfile}>
            <FieldWrapper>
              <Label>ID</Label>
              <Input
                type="text"
                value={username}
                placeholder="ID"
                onChange={onChangeUsername}
                required
              />
            </FieldWrapper>
            <FieldWrapper>
              <Label>E-mail</Label>
              <Input
                type="email"
                value={email}
                placeholder="email@example.com"
                onChange={onChangeEmail}
                required
              />
            </FieldWrapper>
            <FieldWrapper>
              <Label>Team</Label>
              <TooltipWrapper>
                <Input type="text" value={userInfo?.team || ""} disabled />
                <Tooltip
                  left="50%"
                  top="-100%"
                  style={{ transform: "translateX(-50%)", marginTop: "5px" }}
                >
                  You can't change team by yourself. Contact to Admin
                </Tooltip>
              </TooltipWrapper>
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
                  placeholder="Confirm Password"
                  value={confirmPassword}
                  onChange={onChangeConfirmPassword}
                  required
                />
              </FieldWrapper>
              <SubmitPwBtn>Change</SubmitPwBtn>
            </PasswordWrapper>
            <FieldWrapper>
              <Label>Live Until</Label>
              <TooltipWrapper>
                <Input
                  type="datetime-local"
                  value={userInfo?.liveUntil}
                  disabled
                />
                <Tooltip top="calc(100% + 8px)">
                  Expiration date of your account. You can't use the account
                  after this date.
                </Tooltip>
                <Tooltip top="calc(200%)">
                  Contact your account manager for more information.
                </Tooltip>
              </TooltipWrapper>
            </FieldWrapper>
          </Form>
        </ContentWrapper>
      </PageWrapper>
      <PageWrapper>
        <Title>Authorities</Title>
        <ContentWrapper>
          <AuthList>
            {user?.authorities.map((auth, index) => (
              <AuthItem key={index}>{auth}</AuthItem>
            ))}
          </AuthList>
        </ContentWrapper>
      </PageWrapper>
    </Wrapper>
  );
}
