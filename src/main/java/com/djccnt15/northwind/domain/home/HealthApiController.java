package com.djccnt15.northwind.domain.home;

import com.djccnt15.northwind.comm.api.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.djccnt15.northwind.constants.RouteConst.PUBLIC_API_V1;

@Slf4j
@RestController
@RequestMapping(PUBLIC_API_V1)
public class HealthApiController {
    
    @GetMapping("/health")
    public ResponseEntity<Api<Integer>> health() {
        return ResponseEntity.ok(Api.OK(1));
    }
    
    @GetMapping("/ping")
    public ResponseEntity<Api<String>> ping() {
        return ResponseEntity.ok(Api.OK("pong"));
    }
}
