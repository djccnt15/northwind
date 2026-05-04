package com.djccnt15.northwind.config.security.handler;

import com.djccnt15.northwind.db.repository.AppUserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.PUBLIC_API_V1;

@Component
@RequiredArgsConstructor
public class AuthFailureHandler implements AuthenticationFailureHandler {
    
    private final AppUserRepo userRepo;
    
    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) throws IOException, ServletException {
        if (exception instanceof BadCredentialsException) {
            userRepo.findFirstByUsername(request.getParameter("username"))
                .ifPresent(it -> {
                    it.setLoginFailedCount(it.getLoginFailedCount() + 1);
                    userRepo.save(it);
                });
        }

        request.setAttribute("exception", exception);
        request.getRequestDispatcher(PUBLIC_API_V1 + "/auth/login/fail").forward(request, response);
    }
}
