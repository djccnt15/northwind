package com.djccnt15.northwind.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.API_VER_1;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
    
    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) throws IOException, ServletException {
        request.setAttribute("exception", exception);
        request.getRequestDispatcher(API_VER_1 + "/auth/login/fail").forward(request, response);
    }
}
