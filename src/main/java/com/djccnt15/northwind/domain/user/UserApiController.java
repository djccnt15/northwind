package com.djccnt15.northwind.domain.user;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.domain.user.model.SignupReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class UserApiController {
    
    private final UserBusiness userBusiness;
    
    @GetMapping("/check-email")
    public ResponseEntity<Api<?>> checkEmail(@RequestParam String email) {
        userBusiness.checkEmailExists(email);
        return ResponseEntity.ok(Api.OK(null));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Api<?>> createUser(@Validated @RequestBody SignupReq request) {
        userBusiness.createUser(request);
        return ResponseEntity.ok(Api.CREATED(null));
    }
}
