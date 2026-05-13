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
import { useAuth } from "../shared/auth/auth-context";
import { useEffect, useState } from "react";
import { privateApi } from "../shared/api";
import type { ApiIfs } from "../entities/app/api";
import type { UserIfs } from "../entities/employee";

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

  const [firstName, setFirstName] = useState<string>(
    userInfo?.employee?.firstName || "",
  );
  const [lastName, setLastName] = useState<string>(
    userInfo?.employee?.lastName || "",
  );
  const [personalEmail, setPersonalEmail] = useState<string>(
    userInfo?.employee?.email || "",
  );
  const [jobTitle, setJobTitle] = useState<string>(
    userInfo?.employee?.jobTitle || "",
  );
  const [primaryPhone, setPrimaryPhone] = useState<string>(
    userInfo?.employee?.primaryPhone || "",
  );
  const [secondaryPhone, setSecondaryPhone] = useState<string>(
    userInfo?.employee?.secondaryPhone || "",
  );
  const [birthDate, setBirthDate] = useState<string>(
    userInfo?.employee?.birthDate || "",
  );
  const [titleOfCourtesy, setTitleOfCourtesy] = useState<string>(
    userInfo?.employee?.titleOfCourtesy || "",
  );
  const [address, setAddress] = useState<string>(
    userInfo?.employee?.address || "",
  );
  const [city, setCity] = useState<string>(userInfo?.employee?.city || "");
  const [region, setRegion] = useState<string>(
    userInfo?.employee?.region || "",
  );
  const [zipCode, setZipCode] = useState<string>(
    userInfo?.employee?.zipCode || "",
  );
  const [country, setCountry] = useState<string>(
    userInfo?.employee?.country || "",
  );

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

  const onChangeFirstName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFirstName(e.target.value);
  };

  const onChangeLastName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLastName(e.target.value);
  };

  const onChangePersonalEmail = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPersonalEmail(e.target.value);
  };

  const onChangeJobTitle = (e: React.ChangeEvent<HTMLInputElement>) => {
    setJobTitle(e.target.value);
  };

  const onChangePrimaryPhone = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPrimaryPhone(e.target.value);
  };

  const onChangeSecondaryPhone = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSecondaryPhone(e.target.value);
  };

  const onChangeBirthDate = (e: React.ChangeEvent<HTMLInputElement>) => {
    setBirthDate(e.target.value);
  };

  const onChangeTitleOfCourtesy = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTitleOfCourtesy(e.target.value);
  };

  const onChangeAddress = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAddress(e.target.value);
  };

  const onChangeCity = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCity(e.target.value);
  };

  const onChangeRegion = (e: React.ChangeEvent<HTMLInputElement>) => {
    setRegion(e.target.value);
  };

  const onChangeZipCode = (e: React.ChangeEvent<HTMLInputElement>) => {
    setZipCode(e.target.value);
  };

  const onChangeCountry = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCountry(e.target.value);
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

  const onSubmitInfo = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isInfoLoading) return;

    privateApi
      .patch(`/v1/user/${user.id}/info`, {
        firstName,
        lastName,
        email: personalEmail,
        jobTitle,
        primaryPhone,
        secondaryPhone,
        titleOfCourtesy,
        birthDate,
        address,
        city,
        region,
        zipCode,
        country,
      })
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        setUserInfo(data.body || null);
        alert("Personal information updated successfully.");
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
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
              <Input type="date" placeholder="MM/DD/YYYY" disabled />
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
