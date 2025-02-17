package study.blogapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/public")
    public String publicMethod() {
        return " public API";
    }
    @GetMapping("/private")
    public String privateEndpoint() {
        return "Private API - Cần xác thực!";
    }

}
