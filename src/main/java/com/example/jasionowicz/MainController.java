package com.example.jasionowicz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

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

    @GetMapping("/CartMenu")
    public String cart() {
        return "CartMenu";
    }

    @GetMapping("/Profile")
    public String getProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/Login";
        }
        model.addAttribute("username", userDetails.getUsername());
        return "Profile";
    }

    @GetMapping("/auth/getRole")
    public ResponseEntity<Map<String, String>> getUserRole(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("role", "USER"));
        }
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        return ResponseEntity.ok(Map.of("role", role));
    }
}
