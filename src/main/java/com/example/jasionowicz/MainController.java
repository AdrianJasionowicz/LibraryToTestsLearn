package com.example.jasionowicz;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/Menu")
    public String index() {
        return "Menu";
    }

    @GetMapping("/Login")
    public String login() {
        return "Login";
    }

    @GetMapping("/Register")
        public String register() {
        return "Register";
        }
}
