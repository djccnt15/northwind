package com.djccnt15.northwind.domain.user.controller;

import com.djccnt15.northwind.global.api.Api;
import com.djccnt15.northwind.domain.user.business.UserBusiness;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import com.djccnt15.northwind.domain.user.model.UserInfoRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.global.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class UserApiController {
    
    private final UserBusiness business;
    
    @GetMapping("/check-email")
    public ResponseEntity<Api<?>> checkEmail(@RequestParam String email) {
        business.checkEmailExists(email);
        return ResponseEntity.ok(Api.OK(null));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Api<UserInfoRes>> createUser(
        @Validated(SignupReq.CreateCheck.class) @RequestBody SignupReq request
    ) {
        var response = business.createUser(request);
        return ResponseEntity.ok(Api.CREATED(response));
    }
}
