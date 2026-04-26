package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.api.Api;
import com.djccnt15.northwind.domain.auth.model.SignupReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.djccnt15.northwind.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class UserApiController {
    
    private final UserService userService;
    
    @GetMapping("/check-email")
    public ResponseEntity<Api<?>> checkEmail(@RequestParam String email) {
        userService.checkEmailExists(email);
        return ResponseEntity.ok(Api.OK(null));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Api<?>> signup(@RequestBody SignupReq request) {
        userService.signup(request);
        return ResponseEntity.ok(Api.CREATED(null));
    }
}
