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
} from "../shared/ui";
import { useAuth } from "../features/auth";
import { useEffect, useState } from "react";
import { privateApi } from "../shared/api";
import type { ApiIfs } from "../entities/app";
import type { EmployeeIfs, UserIfs } from "../entities";
import { convertEmptyStringToNull } from "../shared/utils";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  min-width: 1200px;
  display: grid;
  grid-template-columns: 1fr 0.4fr;
`;

const PageWrapper = styled.div`
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto 1fr;
  overflow-y: auto;
`;

const ContentWrapper = styled.div`
  padding: 0 20px;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  gap: 15px;
  display: flex;
  flex-direction: column;
`;

const FlexWrapper = styled.div`
  display: flex;
  gap: 10px;
`;

const FlexWrapWrapper = styled(FlexWrapper)`
  flex-wrap: wrap;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const FieldWrapper = styled.div<{ width?: string }>`
  display: flex;
  flex-direction: column;
  width: ${(props) => props.width || "10%"};
`;

const Label = styled.label`
  display: flex;
  white-space: nowrap;
  padding-bottom: 8px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
`;

const Input = styled.input<{ fontSize?: string; height?: string }>`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: ${(props) => props.fontSize || "16px"};
  width: 100%;
  height: ${(props) => props.height || "40px"};
  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const SubmitBtn = styled.button<{
  width?: string;
  fontSize?: string;
  marginTop?: string;
}>`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  height: 40px;
  width: ${(props) => props.width || "100px"};
  margin-top: ${(props) => props.marginTop || "20px"};
  font-size: ${(props) => props.fontSize || "10px"};
  border: none;
  color: white;
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
  const [isInfoLoading, setIsInfoLoading] = useState<boolean>(false);

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

  const updateEmployeeField = (field: keyof EmployeeIfs) => {
    return (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value.trim() === "" ? null : e.target.value;

      setUserInfo((prev) => {
        if (!prev?.employee) {
          return prev;
        }

        const employee = {
          ...prev.employee,
          [field]: value,
        } as unknown as EmployeeIfs;

        return {
          ...prev,
          employee,
        };
      });
    };
  };

  const onChangeFirstName = updateEmployeeField("firstName");
  const onChangeLastName = updateEmployeeField("lastName");
  const onChangePersonalEmail = updateEmployeeField("email");
  const onChangeJobTitle = updateEmployeeField("jobTitle");
  const onChangePrimaryPhone = updateEmployeeField("primaryPhone");
  const onChangeSecondaryPhone = updateEmployeeField("secondaryPhone");
  const onChangeBirthDate = updateEmployeeField("birthDate");
  const onChangeTitleOfCourtesy = updateEmployeeField("titleOfCourtesy");
  const onChangeAddress = updateEmployeeField("address");
  const onChangeCity = updateEmployeeField("city");
  const onChangeRegion = updateEmployeeField("region");
  const onChangeZipCode = updateEmployeeField("zipCode");
  const onChangeCountry = updateEmployeeField("country");

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

  const onSubmitInfo = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isInfoLoading) return;

    const employeeInfo = userInfo?.employee;
    if (!employeeInfo) {
      alert("Employee information is missing.");
      return;
    }

    setIsInfoLoading(true);

    const normalizedInfo = convertEmptyStringToNull(employeeInfo);

    privateApi
      .patch(`/v1/user/${user.id}/info`, normalizedInfo)
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        setUserInfo(data.body || null);
        alert("Personal information updated successfully.");
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;

        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          const message = lines.join("\n");
          alert(
            `Invalid input. Please check your information and try again.\n${message}`,
          );
          return;
        }

        const description = data?.result?.description;
        const message =
          description ||
          "An error occurred while updating personal information.";
        alert(message);
      })
      .finally(() => {
        setIsInfoLoading(false);
      });
  };

  return (
    <Wrapper>
      <PageWrapper>
        <Title>Profile</Title>
        <ContentWrapper>
          <Form onSubmit={onSubmitProfile}>
            <FlexWrapper>
              <FieldWrapper width="100%">
                <Label>ID</Label>
                <Input
                  type="text"
                  value={username}
                  placeholder="ID"
                  onChange={onChangeUsername}
                  required
                />
              </FieldWrapper>
              <FieldWrapper width="100%">
                <Label>E-mail</Label>
                <Input
                  type="email"
                  value={email}
                  placeholder="email@example.com"
                  onChange={onChangeEmail}
                  required
                />
              </FieldWrapper>
              <SubmitBtn>Update</SubmitBtn>
            </FlexWrapper>
          </Form>
          <Form onSubmit={onSubmitPassword}>
            <FlexWrapper>
              <FieldWrapper width="100%">
                <Label>Password</Label>
                <Input
                  type="password"
                  placeholder="Password"
                  value={password}
                  onChange={onChangePassword}
                  required
                />
              </FieldWrapper>
              <FieldWrapper width="100%">
                <Label>Confirm Password</Label>
                <Input
                  type="password"
                  placeholder="Confirm Password"
                  value={confirmPassword}
                  onChange={onChangeConfirmPassword}
                  required
                />
              </FieldWrapper>
              <SubmitBtn>Change</SubmitBtn>
            </FlexWrapper>
          </Form>
          <FlexWrapper>
            <FieldWrapper width="100%">
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
            <FieldWrapper width="100%">
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
            <FieldWrapper width="100%">
              <Label>Title</Label>
              <TooltipWrapper>
                <Input
                  type="text"
                  value={userInfo?.employee?.title || ""}
                  disabled
                />
                <Tooltip
                  left="50%"
                  top="-100%"
                  style={{ transform: "translateX(-50%)", marginTop: "5px" }}
                >
                  You can't change title by yourself. Contact to Admin
                </Tooltip>
              </TooltipWrapper>
            </FieldWrapper>
            <FieldWrapper width="15%">
              <Label>Hire Date</Label>
              <Input
                type="date"
                value={userInfo?.employee?.hireDate || ""}
                disabled
              />
            </FieldWrapper>
          </FlexWrapper>
          <Form onSubmit={onSubmitInfo}>
            <FlexWrapWrapper>
              <FieldWrapper width="10%">
                <Label>First Name</Label>
                <Input
                  type="text"
                  placeholder="John"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeFirstName}
                  value={userInfo?.employee?.firstName || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Last Name</Label>
                <Input
                  type="text"
                  placeholder="Doe"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeLastName}
                  value={userInfo?.employee?.lastName || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>E-mail</Label>
                <Input
                  type="text"
                  placeholder="john.doe@example.com"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangePersonalEmail}
                  value={userInfo?.employee?.email || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Job Title</Label>
                <Input
                  type="text"
                  placeholder="Senior Engineer"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeJobTitle}
                  value={userInfo?.employee?.jobTitle || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Phone 1</Label>
                <Input
                  type="text"
                  placeholder="(123) 456-7890"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangePrimaryPhone}
                  value={userInfo?.employee?.primaryPhone || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Phone 2</Label>
                <Input
                  type="text"
                  placeholder="(123) 456-7890"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeSecondaryPhone}
                  value={userInfo?.employee?.secondaryPhone || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Birth Date</Label>
                <Input
                  type="date"
                  placeholder="MM/DD/YYYY"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeBirthDate}
                  value={userInfo?.employee?.birthDate || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Courtesy Title</Label>
                <Input
                  type="text"
                  placeholder="Mr., Ms., Dr., etc."
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeTitleOfCourtesy}
                  value={userInfo?.employee?.titleOfCourtesy || ""}
                />
              </FieldWrapper>
            </FlexWrapWrapper>
            <FlexWrapWrapper>
              <FieldWrapper width="10%">
                <Label>Address</Label>
                <Input
                  type="text"
                  placeholder="123 Main St, City, Country"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeAddress}
                  value={userInfo?.employee?.address || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>City</Label>
                <Input
                  type="text"
                  placeholder="City"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeCity}
                  value={userInfo?.employee?.city || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Region</Label>
                <Input
                  type="text"
                  placeholder="Region/State"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeRegion}
                  value={userInfo?.employee?.region || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Zip Code</Label>
                <Input
                  type="text"
                  placeholder="Zip Code"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeZipCode}
                  value={userInfo?.employee?.zipCode || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>Country</Label>
                <Input
                  type="text"
                  placeholder="Country"
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeCountry}
                  value={userInfo?.employee?.country || ""}
                />
              </FieldWrapper>
            </FlexWrapWrapper>
            <SubmitBtn width="100%" fontSize="16px" marginTop="10px">
              Update Profile
            </SubmitBtn>
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
