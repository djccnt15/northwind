package com.djccnt15.northwind.domain.home;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {
    
    // temporal redirect to swagger open api
    @GetMapping
    public String home() {
        return "redirect:swagger-ui/index.html";
    }
}
