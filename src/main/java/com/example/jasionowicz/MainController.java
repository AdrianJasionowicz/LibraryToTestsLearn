package com.example.jasionowicz;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/Cart")
    public String cart() {
        return "Cart";
    }

    @GetMapping("/Profile")
    public String getProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/Login";
        }
        model.addAttribute("username", userDetails.getUsername());
        return "Profile";
    }
}
