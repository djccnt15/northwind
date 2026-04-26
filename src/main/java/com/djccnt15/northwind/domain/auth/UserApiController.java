package com.djccnt15.northwind.domain.auth;

import com.djccnt15.northwind.comm.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
