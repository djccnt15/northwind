import styled from "styled-components";
import { useTranslation } from "react-i18next";
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
import i18n from "../shared/i18n";
import type { ApiIfs } from "../entities/app";
import type { EmployeeIfs, LangIfs, UserIfs } from "../entities";
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

const Select = styled.select<{ fontSize?: string; height?: string }>`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: ${(props) => props.fontSize || "16px"};
  width: 100%;
  height: ${(props) => props.height || "40px"};
  background-color: white;
  cursor: pointer;
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
  const { t } = useTranslation();
  const { user, setUser } = useAuth();

  const [username, setUsername] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [userInfo, setUserInfo] = useState<UserIfs | null>(null);

  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");

  const [langs, setLangs] = useState<LangIfs[]>([]);
  const [preferredLangId, setPreferredLangId] = useState<number | "">("");

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isPasswordLoading, setIsPasswordLoading] = useState<boolean>(false);
  const [isInfoLoading, setIsInfoLoading] = useState<boolean>(false);
  const [isLangLoading, setIsLangLoading] = useState<boolean>(false);

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
          alert(t("page.profile.alerts.fetchFailed"));
        });
    };

    fetchUserInfo();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]);

  useEffect(() => {
    const fetchLangs = async () => {
      privateApi
        .get("/v1/lang")
        .then((res) => {
          const data: ApiIfs<LangIfs[]> = res.data;
          setLangs(data.body || []);
        })
        .catch((err) => {
          console.error("Failed to fetch languages:", err);
        });
    };

    fetchLangs();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    const currentLang = langs.find(
      (lang) => lang.lang === userInfo?.preferredLang
    );
    setPreferredLangId(currentLang?.id ?? "");
  }, [langs, userInfo?.preferredLang]);

  const onChangePreferredLang = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setPreferredLangId(e.target.value === "" ? "" : Number(e.target.value));
  };

  const onSubmitProfile = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isLoading) return;

    if (username === userInfo?.username && email === userInfo?.email) {
      alert(t("page.profile.alerts.noChanges"));
      return;
    }

    if (username.trim() === "" || email.trim() === "") {
      alert(t("page.profile.alerts.usernameEmailEmpty"));
      return;
    }

    setIsLoading(true);

    privateApi
      .patch(`/v1/user/${user.id}/profile`, { username, email })
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        setUserInfo(data.body || null);
        setUser({ ...user, username: data.body?.username || user.username });
        alert(t("page.profile.alerts.profileUpdated"));
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message =
          description || t("page.profile.alerts.profileUpdateFailed");
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
      alert(t("page.profile.alerts.passwordEmpty"));
      return;
    }

    if (password !== confirmPassword) {
      alert(t("page.profile.alerts.passwordMismatch"));
      return;
    }

    setIsPasswordLoading(true);

    privateApi
      .patch(`/v1/user/${user.id}/password`, { password, confirmPassword })
      .then(() => {
        alert(t("page.profile.alerts.passwordUpdated"));
        setPassword("");
        setConfirmPassword("");
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message =
          description || t("page.profile.alerts.passwordUpdateFailed");
        alert(message);
      })
      .finally(() => {
        setIsPasswordLoading(false);
      });
  };

  const onSubmitLang = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isLangLoading) return;

    if (preferredLangId === "") {
      alert(t("page.profile.alerts.langEmpty"));
      return;
    }

    setIsLangLoading(true);

    privateApi
      .patch(`/v1/user/${user.id}/lang`, { preferredLangId })
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        setUserInfo(data.body || null);
        const updatedLang = data.body?.preferredLang;
        if (updatedLang) {
          i18n.changeLanguage(updatedLang);
        }
        alert(t("page.profile.alerts.langUpdated"));
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;
        const description = data?.result?.description;
        const message = description || t("page.profile.alerts.langUpdateFailed");
        alert(message);
      })
      .finally(() => {
        setIsLangLoading(false);
      });
  };

  const onSubmitInfo = (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!user || isInfoLoading) return;

    const employeeInfo = userInfo?.employee;
    if (!employeeInfo) {
      alert(t("page.profile.alerts.employeeMissing"));
      return;
    }

    setIsInfoLoading(true);

    const normalizedInfo = convertEmptyStringToNull(employeeInfo);

    privateApi
      .patch(`/v1/user/${user.id}/info`, normalizedInfo)
      .then((res) => {
        const data: ApiIfs<UserIfs> = res.data;
        setUserInfo(data.body || null);
        alert(t("page.profile.alerts.personalUpdated"));
      })
      .catch((err) => {
        console.error(err);
        const data: ApiIfs<null> = err.response?.data;

        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          const message = lines.join("\n");
          alert(`${t("page.profile.alerts.invalidInput")}\n${message}`);
          return;
        }

        const description = data?.result?.description;
        const message =
          description || t("page.profile.alerts.personalUpdateFailed");
        alert(message);
      })
      .finally(() => {
        setIsInfoLoading(false);
      });
  };

  return (
    <Wrapper>
      <PageWrapper>
        <Title>{t("page.profile.title")}</Title>
        <ContentWrapper>
          <Form onSubmit={onSubmitProfile}>
            <FlexWrapper>
              <FieldWrapper width="100%">
                <Label>{t("page.profile.fields.id")}</Label>
                <Input
                  type="text"
                  value={username}
                  placeholder={t("page.profile.placeholders.id")}
                  onChange={onChangeUsername}
                  required
                />
              </FieldWrapper>
              <FieldWrapper width="100%">
                <Label>{t("page.profile.fields.email")}</Label>
                <Input
                  type="email"
                  value={email}
                  placeholder={t("page.profile.placeholders.email")}
                  onChange={onChangeEmail}
                  required
                />
              </FieldWrapper>
              <SubmitBtn>{t("page.profile.update")}</SubmitBtn>
            </FlexWrapper>
          </Form>
          <Form onSubmit={onSubmitPassword}>
            <FlexWrapper>
              <FieldWrapper width="100%">
                <Label>{t("page.profile.fields.password")}</Label>
                <Input
                  type="password"
                  placeholder={t("page.profile.fields.password")}
                  value={password}
                  onChange={onChangePassword}
                  required
                />
              </FieldWrapper>
              <FieldWrapper width="100%">
                <Label>{t("page.profile.fields.confirmPassword")}</Label>
                <Input
                  type="password"
                  placeholder={t("page.profile.fields.confirmPassword")}
                  value={confirmPassword}
                  onChange={onChangeConfirmPassword}
                  required
                />
              </FieldWrapper>
              <SubmitBtn>{t("page.profile.change")}</SubmitBtn>
            </FlexWrapper>
          </Form>
          <Form onSubmit={onSubmitLang}>
            <FlexWrapper>
              <FieldWrapper width="100%">
                <Label>{t("page.profile.fields.language")}</Label>
                <Select
                  value={preferredLangId}
                  onChange={onChangePreferredLang}
                  required
                >
                  <option value="" disabled>
                    {t("page.profile.placeholders.language")}
                  </option>
                  {langs.map((lang) => (
                    <option key={lang.id} value={lang.id}>
                      {t(`page.profile.languages.${lang.lang}`, lang.lang)}
                    </option>
                  ))}
                </Select>
              </FieldWrapper>
              <SubmitBtn>{t("page.profile.change")}</SubmitBtn>
            </FlexWrapper>
          </Form>
          <FlexWrapper>
            <FieldWrapper width="100%">
              <Label>{t("page.profile.fields.liveUntil")}</Label>
              <TooltipWrapper>
                <Input
                  type="datetime-local"
                  value={userInfo?.liveUntil}
                  disabled
                />
                <Tooltip top="calc(100% + 8px)">
                  {t("page.profile.tooltips.liveUntil")}
                </Tooltip>
                <Tooltip top="calc(200%)">
                  {t("page.profile.tooltips.contactManager")}
                </Tooltip>
              </TooltipWrapper>
            </FieldWrapper>
            <FieldWrapper width="100%">
              <Label>{t("page.profile.fields.team")}</Label>
              <TooltipWrapper>
                <Input type="text" value={userInfo?.team || ""} disabled />
                <Tooltip
                  left="50%"
                  top="-100%"
                  style={{ transform: "translateX(-50%)", marginTop: "5px" }}
                >
                  {t("page.profile.tooltips.team")}
                </Tooltip>
              </TooltipWrapper>
            </FieldWrapper>
            <FieldWrapper width="100%">
              <Label>{t("page.profile.fields.title")}</Label>
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
                  {t("page.profile.tooltips.title")}
                </Tooltip>
              </TooltipWrapper>
            </FieldWrapper>
            <FieldWrapper width="15%">
              <Label>{t("page.profile.fields.hireDate")}</Label>
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
                <Label>{t("page.profile.fields.firstName")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.firstName")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeFirstName}
                  value={userInfo?.employee?.firstName || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.lastName")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.lastName")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeLastName}
                  value={userInfo?.employee?.lastName || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.email")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.personalEmail")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangePersonalEmail}
                  value={userInfo?.employee?.email || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.jobTitle")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.jobTitle")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeJobTitle}
                  value={userInfo?.employee?.jobTitle || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.phone1")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.phone1")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangePrimaryPhone}
                  value={userInfo?.employee?.primaryPhone || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.phone2")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.phone2")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeSecondaryPhone}
                  value={userInfo?.employee?.secondaryPhone || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.birthDate")}</Label>
                <Input
                  type="date"
                  placeholder={t("page.profile.placeholders.birthDate")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeBirthDate}
                  value={userInfo?.employee?.birthDate || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.courtesyTitle")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.courtesyTitle")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeTitleOfCourtesy}
                  value={userInfo?.employee?.titleOfCourtesy || ""}
                />
              </FieldWrapper>
            </FlexWrapWrapper>
            <FlexWrapWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.address")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.address")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeAddress}
                  value={userInfo?.employee?.address || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.city")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.city")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeCity}
                  value={userInfo?.employee?.city || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.region")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.region")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeRegion}
                  value={userInfo?.employee?.region || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.zipCode")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.zipCode")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeZipCode}
                  value={userInfo?.employee?.zipCode || ""}
                />
              </FieldWrapper>
              <FieldWrapper width="10%">
                <Label>{t("page.profile.fields.country")}</Label>
                <Input
                  type="text"
                  placeholder={t("page.profile.placeholders.country")}
                  fontSize="12px"
                  height="30px"
                  onChange={onChangeCountry}
                  value={userInfo?.employee?.country || ""}
                />
              </FieldWrapper>
            </FlexWrapWrapper>
            <SubmitBtn width="100%" fontSize="16px" marginTop="10px">
              {t("page.profile.updateProfile")}
            </SubmitBtn>
          </Form>
        </ContentWrapper>
      </PageWrapper>
      <PageWrapper>
        <Title>{t("page.profile.authorities")}</Title>
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
