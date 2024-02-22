package org.spring.framework.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSessionController {

    @GetMapping("/hello")
    public String hello(HttpSession httpSession){
        return "hello";
    }
}
