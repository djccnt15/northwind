import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { api } from "../shared/api";
import i18n from "../shared/i18n";
import {
  ErrorMsg,
  Form,
  H1,
  Input,
  SubmitBtn,
  SubmitBtnHoverMsg,
  SubmitBtnWrapper,
  Switcher,
} from "../shared/ui";
import { responseToUser, useAuth } from "../features/auth";
import type { ApiIfs, SessionIfs } from "../entities/app";

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
  const { t } = useTranslation();
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
        const data: ApiIfs<SessionIfs> = res.data;
        console.log("login response:", data);
        const sessionUser = responseToUser(data);
        setUser(sessionUser);
        if (sessionUser.preferredLang && i18n.language !== sessionUser.preferredLang) {
          i18n.changeLanguage(sessionUser.preferredLang);
        }

        if (rememberId) {
          localStorage.setItem("rememberedId", username);
        } else {
          localStorage.removeItem("rememberedId");
        }

        const from = location.state?.from?.pathname || "/";
        navigate(from, { replace: true });
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        console.error(data || err);
        const description = data?.result?.description;
        const message = description
          ? t("auth.login.failedWithReason", { reason: description })
          : t("auth.login.failedDefault");
        setErrorMsg(message);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <Wrapper>
      <H1>{t("auth.login.title")}</H1>
      <Form onSubmit={onSubmit}>
        <Input
          type="text"
          placeholder={t("auth.login.idPlaceholder")}
          value={username}
          onChange={onChangeUsername}
          required
        />
        <Input
          type="password"
          placeholder={t("auth.login.passwordPlaceholder")}
          value={password}
          onChange={onChangePassword}
          required
        />
        <SubmitBtnWrapper>
          <SubmitBtn disabled={isLoading}>
            {isLoading ? t("auth.login.loading") : t("auth.login.submit")}
          </SubmitBtn>
          {isLoading && (
            <SubmitBtnHoverMsg>{t("auth.login.submitting")}</SubmitBtnHoverMsg>
          )}
        </SubmitBtnWrapper>
        <CheckBoxArea>
          <CheckBoxWrapper>
            <CheckBoxInput
              type="checkbox"
              id="rememberMe"
              checked={rememberMe}
              onChange={onChangeRememberMe}
            />
            <Label htmlFor="rememberMe">{t("auth.login.rememberMe")}</Label>
          </CheckBoxWrapper>
          <CheckBoxWrapper>
            <CheckBoxInput
              type="checkbox"
              id="rememberId"
              checked={rememberId}
              onChange={(e) => setRememberId(e.target.checked)}
            />
            <Label htmlFor="rememberId">{t("auth.login.rememberId")}</Label>
          </CheckBoxWrapper>
        </CheckBoxArea>
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        {t("auth.login.noAccount")}{" "}
        <Link to="/signup">{t("auth.login.createOne")}</Link>
      </Switcher>
    </Wrapper>
  );
}
