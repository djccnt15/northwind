import { useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
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
  commBorderRadius,
  commBtnSkyBlue,
  commBtnHoverSkyBlue,
  commBtnSkyBlueBoxShadow,
  ModalOverlay,
  commLinkSkyBlue,
  globalTransition,
  ModalDefault,
  commBtnTomatoRed,
  commBtnTomatoRedBoxShadow,
  commBtnHoverTomatoRed,
} from "../shared/ui";
import type { ApiIfs } from "../entities/app";
import type { UserIfs } from "../entities";
import { useKeyDown } from "../shared/useKeyDown";

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

const ErrorModal = styled(ModalDefault)`
  width: 500px;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  font-size: 16px;
  font-weight: 500;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
`;

const ModalTitle = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const ModalHeader = styled.h2`
  font-size: 20px;
  font-weight: 700;
`;

const ModalCloseBtn = styled.button`
  ${commBtnTomatoRed}
  ${globalTransition}
  ${commBorderRadius}
  height: 30px;
  width: 40px;
  border: none;
  color: white;
  font-size: 10px;
  cursor: pointer;

  &:hover {
    ${commBtnHoverTomatoRed}
    ${globalTransition}
  }

  &:focus {
    outline: none;
    ${commBtnTomatoRedBoxShadow}
  }
`;

const ModalContent = styled.div`
  text-align: left;
`;

export default function Signup() {
  const { t } = useTranslation();
  const [errorMsg, setErrorMsg] = useState<string>("");
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isEmailChecked, setIsEmailChecked] = useState<boolean>(false);
  const [isSignupSuccess, setIsSignupSuccess] = useState<boolean>(false);
  const [isErrorModalOpen, setIsErrorModalOpen] = useState<boolean>(false);

  const [username, setUsername] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");

  const closeErrorModal = () => {
    setIsErrorModalOpen(false);
    setErrorMsg("");
  };

  useKeyDown("Escape", () => {
    if (isErrorModalOpen) {
      closeErrorModal();
    }
  });

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
      alert(t("auth.signup.enterEmail"));
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
          ? t("auth.signup.failedCheckEmailWithReason", { reason: description })
          : t("auth.signup.failedCheckEmail");
        alert(message);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const onSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (isLoading || isErrorModalOpen) return;

    if (username.trim() === "" || email.trim() === "") {
      alert(t("auth.signup.emptyUsernameEmail"));
      return;
    }

    if (password !== confirmPassword) {
      alert(t("auth.signup.passwordMismatch"));
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

        if (data?.result?.code === 1400) {
          const validationErrors = data?.body || {};
          const messages = Object.values(validationErrors) as string[];
          setValidationErrors(messages);
          setIsErrorModalOpen(true);
          return;
        }

        const description = data?.result?.description;
        const message = description
          ? t("auth.signup.signupFailedWithReason", { reason: description })
          : t("auth.signup.signupFailedDefault");
        setErrorMsg(message);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const isSubmitDisabled = isLoading || !isEmailChecked;
  const submitDisabledMessage = isLoading
    ? t("auth.signup.submitting")
    : !isEmailChecked
      ? t("auth.signup.checkEmailHint")
      : "";

  return (
    <Wrapper>
      <H1>{t("auth.signup.title")}</H1>
      <Form onSubmit={onSubmit}>
        <Input
          type="text"
          placeholder={t("auth.signup.idPlaceholder")}
          value={username}
          onChange={onChangeUsername}
          required
        />
        <EmailCheckWrapper>
          <Input
            type="email"
            placeholder={t("auth.signup.emailPlaceholder")}
            value={email}
            onChange={onChangeEmail}
            required
          />
          <CheckEmailBtn type="button" onClick={onClickCheckEmail}>
            {t("auth.signup.checkEmail")}
          </CheckEmailBtn>
        </EmailCheckWrapper>
        <Input
          type="password"
          placeholder={t("auth.signup.passwordPlaceholder")}
          value={password}
          onChange={onChangePassword}
          required
        />
        <Input
          type="password"
          placeholder={t("auth.signup.confirmPasswordPlaceholder")}
          value={confirmPassword}
          onChange={onChangeConfirmPassword}
          required
        />
        <SubmitBtnWrapper>
          <SubmitBtn disabled={isSubmitDisabled}>
            {isLoading ? t("auth.signup.loading") : t("auth.signup.submit")}
          </SubmitBtn>
          {isSubmitDisabled && (
            <SubmitBtnHoverMsg>{submitDisabledMessage}</SubmitBtnHoverMsg>
          )}
        </SubmitBtnWrapper>
      </Form>
      {errorMsg && <ErrorMsg>{errorMsg}</ErrorMsg>}
      <Switcher>
        {t("auth.signup.alreadyHaveAccount")}{" "}
        <Link to="/login">{t("auth.signup.logIn")}</Link>
      </Switcher>
      {isSignupSuccess && (
        <ModalOverlay>
          <SuccessModal>
            {t("auth.signup.successMessage")}{" "}
            <Link to="/login">{t("auth.signup.successLink")}</Link>
          </SuccessModal>
        </ModalOverlay>
      )}
      {isErrorModalOpen && (
        <ModalOverlay>
          <ErrorModal>
            <ModalTitle>
              <ModalHeader>{t("auth.signup.failedModalTitle")}</ModalHeader>
              <ModalCloseBtn onClick={closeErrorModal}>X</ModalCloseBtn>
            </ModalTitle>
            <ModalContent>
              {validationErrors.map((msg, index) => (
                <p key={index}>{msg}</p>
              ))}
            </ModalContent>
          </ErrorModal>
        </ModalOverlay>
      )}
    </Wrapper>
  );
}
