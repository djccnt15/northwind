package com.djccnt15.northwind.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.API_VER_1;

@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException, ServletException {
        request.setAttribute("exception", authException);
        request.getRequestDispatcher(API_VER_1 + "/auth/unauthorized").forward(request, response);
    }
}
