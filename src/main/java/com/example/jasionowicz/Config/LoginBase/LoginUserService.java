package com.example.jasionowicz.Config.LoginBase;

import com.example.jasionowicz.Config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private LoginUserRepository loginUserRepository;


    public LoginUserService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, LoginUserRepository loginUserRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginUserRepository = loginUserRepository;
    }

    public ResponseEntity   makeNewLoginUser(LoginUserDTO loginUserDTO) {
        LoginUser loginUser2 = new LoginUser();
        loginUser2.setPassword(passwordEncoder.encode(loginUserDTO.getPassword()));
        loginUser2.setUsername(loginUserDTO.getUsername());
        loginUserRepository.save(loginUser2);
        return ResponseEntity.ok().body("User has been made");
    }

    public String authenticateUser(LoginUser loginUser) {
        LoginUser existingUser = loginUserRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
        System.out.println("Czy hasła pasują? " + passwordEncoder.matches(loginUser.getPassword(), existingUser.getPassword()));

        if (!passwordEncoder.matches(loginUser.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Nieprawidłowe hasło!");
        }
        return jwtUtil.generateToken(existingUser.getUsername());
    }


    public LoginUserDTO converLoginUserToDTO(LoginUser loginUser) {
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername(loginUser.getUsername());
        loginUserDTO.setPassword(loginUser.getPassword());
        return loginUserDTO;
    }

    public void converDtoToLoginUserAndSave(LoginUserDTO loginUserDTO) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(loginUserDTO.getUsername());
        loginUser.setPassword(loginUserDTO.getPassword());
        loginUserRepository.save(loginUser);
    }



}