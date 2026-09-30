package com.djccnt15.northwind.domain.auth.controller;

import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.global.config.security.AuthBusiness;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.djccnt15.northwind.domain.auth.validation.AuthErrorConst.ACCESS_DENIED_ERR_MSG;
import static com.djccnt15.northwind.domain.auth.validation.AuthErrorConst.AUTHENTICATION_REQUIRED_ERR_MSG;
import static com.djccnt15.northwind.global.code.StatusCode.FORBIDDEN;
import static com.djccnt15.northwind.global.code.StatusCode.UNAUTHORIZED;
import static com.djccnt15.northwind.global.constants.RouteConst.PUBLIC_API_V1;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@RequestMapping(PUBLIC_API_V1 + "/auth")
@RequiredArgsConstructor
public class AuthPublicApiController {
    
    private final AuthBusiness business;
    private final MessageUtil messageUtil;

    @RequestMapping("/login/fail")
    public void loginFail(HttpServletRequest request) {
        business.handleLoginFailureInController(request);
    }

    @RequestMapping(value = "/unauthorized", method = {GET, POST, PUT, DELETE})
    public ResponseEntity<Api<?>> unauthorized() {
        throw new ApiException(UNAUTHORIZED, messageUtil.getMessage(AUTHENTICATION_REQUIRED_ERR_MSG));
    }

    @RequestMapping(value = "/forbidden", method = {GET, POST, PUT, DELETE})
    public ResponseEntity<Api<?>> forbidden() {
        throw new ApiException(FORBIDDEN, messageUtil.getMessage(ACCESS_DENIED_ERR_MSG));
    }
    
    @GetMapping("/csrf-token")
    public ResponseEntity<Api<Void>> getCsrfToken(CsrfToken csrfToken) {
        return ResponseEntity.ok(Api.OK(null));
    }
}
