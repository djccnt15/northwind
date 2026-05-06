package com.djccnt15.northwind.global.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.global.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@Component
public class ForbiddenHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
        HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        request.setAttribute("exception", accessDeniedException);
        request.getRequestDispatcher(PUBLIC_API_V1 + "/auth/forbidden").forward(request, response);
    }
}
