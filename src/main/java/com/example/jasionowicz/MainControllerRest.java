package com.example.jasionowicz;

import com.example.jasionowicz.Config.LoginBase.LoginUser;
import com.example.jasionowicz.Config.LoginBase.LoginUserDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class MainControllerRest {
    private final LoginUserService loginUserService;
    private final PasswordEncoder passwordEncoder;


    public MainControllerRest(LoginUserService loginUserService, PasswordEncoder passwordEncoder) {
        this.loginUserService = loginUserService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LoginUserDTO loginUserDTO) {
        return loginUserService.makeNewLoginUser(loginUserDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUser loginUser) {
        try {
            String token = loginUserService.authenticateUser(loginUser);
            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        }
    }

}
