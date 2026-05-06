package com.djccnt15.northwind.global.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.global.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException, ServletException {
        request.setAttribute("exception", authException);
        request.getRequestDispatcher(PUBLIC_API_V1 + "/auth/unauthorized").forward(request, response);
    }
}
