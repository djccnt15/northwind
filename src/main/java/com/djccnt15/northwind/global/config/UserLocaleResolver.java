package com.djccnt15.northwind.global.config;

import com.djccnt15.northwind.global.config.security.model.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;
import java.util.Optional;

/**
 * 인증 요청은 {@link UserSession#getPreferredLang()}로 Locale을 결정하고,
 * 비인증 요청 또는 선호 언어가 없는 경우 Accept-Language 헤더 기반으로 폴백한다.
 * Spring MVC는 빈 이름 {@code localeResolver}를 자동 탐지하므로
 * {@link WebConfig}에서 {@code @Bean}으로 등록한다.
 */
public class UserLocaleResolver implements LocaleResolver {

    private final AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();

    @Override
    @NonNull
    public Locale resolveLocale(@NonNull HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(UserSession.class::isInstance)
            .map(UserSession.class::cast)
            .map(UserSession::getPreferredLang)
            .filter(lang -> !lang.isBlank())
            .map(Locale::forLanguageTag)
            .orElseGet(() -> acceptHeaderLocaleResolver.resolveLocale(request));
    }

    @Override
    public void setLocale(
        @NonNull HttpServletRequest request,
        @Nullable HttpServletResponse response,
        @Nullable Locale locale
    ) {
        // no-op: 선호 언어 변경은 UserBusiness.updateLang이 담당한다
    }
}
