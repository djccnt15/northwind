import styled from "styled-components";
import { useTranslation } from "react-i18next";
import { useAuth } from "../features/auth";

const Wrapper = styled.div``;

const H1 = styled.h1``;

const P = styled.p``;

export default function Home() {
  const { t } = useTranslation();
  const { user } = useAuth();

  return (
    <Wrapper>
      <H1>{t("page.home.title", { username: user?.username })}</H1>
      <P>{t("page.home.description")}</P>
    </Wrapper>
  );
}
