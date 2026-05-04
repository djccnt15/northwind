package com.djccnt15.northwind.config.security.handler;

import com.djccnt15.northwind.db.repository.AppUserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.djccnt15.northwind.constants.RouteConst.API_V1;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    
    private final AppUserRepo userRepo;
    
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        userRepo.findFirstByUsername(request.getParameter("username"))
            .ifPresent(it -> {
                it.setLoginFailedCount(0);
                userRepo.save(it);
            });
        
        request.getRequestDispatcher(API_V1 + "/auth/login/success").forward(request, response);
    }
}
