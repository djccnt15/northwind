package com.djccnt15.northwind.config.security;

import com.djccnt15.northwind.annotation.Business;
import com.djccnt15.northwind.config.security.model.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@Business
@RequiredArgsConstructor
public class AuthBusiness {
    
    private final AuthService service;
    
    public void handleLoginSuccess(Authentication authentication) {
        var userSession = (UserSession) authentication.getPrincipal();
        service.handleLoginSuccess(userSession.getId());
    }
    
    public void handleLoginFailure(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) throws ServletException, IOException {
        if (exception instanceof BadCredentialsException) {
            service.increaseFailedCount(request.getParameter("username"));
        }
        request.setAttribute("exception", exception);
        request.getRequestDispatcher(PUBLIC_API_V1 + "/auth/login/fail").forward(request, response);
    }
    
    public void handleLoginFailureInController(HttpServletRequest request) {
        var exception = (AuthenticationException) request.getAttribute("exception");
        var message = service.getErrorMessage(exception);
        service.throwException(exception, message);
    }
}
