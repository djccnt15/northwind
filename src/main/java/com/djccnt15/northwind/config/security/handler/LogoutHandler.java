package com.djccnt15.northwind.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.PUBLIC_API_V1;

@Component
public class LogoutHandler implements LogoutSuccessHandler {
    
    @Override
    public void onLogoutSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        request.getRequestDispatcher(PUBLIC_API_V1 + "/auth/logout").forward(request, response);
    }
}
