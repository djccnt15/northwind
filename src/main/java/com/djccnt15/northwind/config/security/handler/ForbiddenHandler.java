package com.djccnt15.northwind.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.API_VER_1;

@Component
public class ForbiddenHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
        HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        request.setAttribute("exception", accessDeniedException);
        request.getRequestDispatcher(API_VER_1 + "/auth/forbidden").forward(request, response);
    }
}
