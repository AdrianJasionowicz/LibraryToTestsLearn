package com.example.jasionowicz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<Map<String, List<String>>> getUserRoles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("roles", List.of("USER")));
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("roles", roles));
    }

}
