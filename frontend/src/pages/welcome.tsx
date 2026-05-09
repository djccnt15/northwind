import { Link } from "react-router-dom";
import styled from "styled-components";
import { commLinkSkyBlue } from "../shared/ui/global-styles";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto 1fr auto;
`;

const H1 = styled.h1`
  font-size: 2.5rem;
  font-weight: 700;
  text-transform: uppercase;
  padding: 4%;
`;

const ContentArea = styled.div`
  display: grid;
  flex-direction: row;
  grid-template-rows: auto 1fr;

  img {
    width: 100%;
  }
`;

const DescriptionArea = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const SloganArea = styled.div`
  font-size: 10vw;
  padding: 0 4%;
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

const SloganRow = styled.div``;

const P = styled.p`
  padding: 0 4%;
`;

const Footer = styled.footer`
  padding: 0rem 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: black;
  background-color: #efefef;
`;

const FooterFromLeft = styled.div`
  display: flex;
  gap: 1rem;
`;

const FooterFromRight = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
`;

const InternalLink = styled(Link)`
  ${commLinkSkyBlue}
`;

const ExternalLink = styled.a`
  color: black;
`;

const FooterLink = styled(ExternalLink)`
  padding: 1rem 0;
  display: flex;
  gap: 5px;

  svg {
    width: 15px;
    color: black;
  }
`;

export default function Welcome() {
  return (
    <Wrapper>
      <H1>Welcome to Northwind!</H1>
      <ContentArea>
        <DescriptionArea>
          <P>
            Northwind is a sample ERP application built with{" "}
            <ExternalLink
              href="https://react.dev/"
              target="_blank"
              rel="noopener noreferrer"
            >
              React
            </ExternalLink>{" "}
            and{" "}
            <ExternalLink
              href="https://spring.io/"
              target="_blank"
              rel="noopener noreferrer"
            >
              Spring
            </ExternalLink>
            .
          </P>
          <P>
            It is a great starting point for learning how to build a full-stack
            application with{" "}
            <ExternalLink
              href="https://www.typescriptlang.org/"
              target="_blank"
              rel="noopener noreferrer"
            >
              TypeScript
            </ExternalLink>{" "}
            and{" "}
            <ExternalLink
              href="https://www.java.com/en/"
              target="_blank"
              rel="noopener noreferrer"
            >
              Java
            </ExternalLink>
            .
          </P>
          <P>
            Please <InternalLink to="/login">login</InternalLink> or{" "}
            <InternalLink to="/signup">sign up</InternalLink> to continue.
          </P>
        </DescriptionArea>
        <SloganArea>
          <SloganRow>Standing on the</SloganRow>
          <SloganRow>Shoulders of Giants</SloganRow>
        </SloganArea>
      </ContentArea>
      <Footer>
        <FooterFromLeft>
          &copy; 2024 Northwind. All rights reserved.
        </FooterFromLeft>
        <FooterFromRight>
          <FooterLink
            href="https://djccnt15.github.io/"
            target="_blank"
            rel="noopener noreferrer"
          >
            <svg
              role="img"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <title>GitHub</title>
              <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12" />
            </svg>
            GitHub
          </FooterLink>
          <FooterLink
            href="https://github.com/djccnt15"
            target="_blank"
            rel="noopener noreferrer"
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
              <path d="M341.8 72.6C329.5 61.2 310.5 61.2 298.3 72.6L74.3 280.6C64.7 289.6 61.5 303.5 66.3 315.7C71.1 327.9 82.8 336 96 336L112 336L112 512C112 547.3 140.7 576 176 576L464 576C499.3 576 528 547.3 528 512L528 336L544 336C557.2 336 569 327.9 573.8 315.7C578.6 303.5 575.4 289.5 565.8 280.6L341.8 72.6zM304 384L336 384C362.5 384 384 405.5 384 432L384 528L256 528L256 432C256 405.5 277.5 384 304 384z" />
            </svg>
            Blog
          </FooterLink>
        </FooterFromRight>
      </Footer>
    </Wrapper>
  );
}
