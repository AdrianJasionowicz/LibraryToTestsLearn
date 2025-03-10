package com.example.jasionowicz.Config.LoginBase;

import com.example.jasionowicz.Config.JwtUtil;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserRepository;
import com.example.jasionowicz.User.LibraryUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginUserService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LoginUserRepository loginUserRepository;
    private final LibraryUserRepository libraryUserRepository;


    public LoginUserService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, LoginUserRepository loginUserRepository, LibraryUserRepository libraryUserRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginUserRepository = loginUserRepository;
        this.libraryUserRepository = libraryUserRepository;
    }

    public ResponseEntity<?> makeNewLoginUser(LoginUserDTO loginUserDTO) {
        if (loginUserRepository.findByUsername(loginUserDTO.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Użytkownik o tej nazwie już istnieje!");
        }

        LoginUser loginUser2 = new LoginUser();
        loginUser2.setPassword(passwordEncoder.encode(loginUserDTO.getPassword()));
        loginUser2.setUsername(loginUserDTO.getUsername());
        loginUser2.setRole(LoginUserRole.ROLE_USER);
        loginUserRepository.save(loginUser2);

        LibraryUser libraryUser = new LibraryUser();
        libraryUser.setName("None");
        libraryUser.setEmail("None");
        libraryUser.setAccountBalance(0);
        libraryUser.setLoginUser(loginUser2);

        loginUser2.setLibraryUser(libraryUser);

        libraryUserRepository.save(libraryUser);



        return ResponseEntity.ok().body("User has been made");
    }


    public String authenticateUser(LoginUser loginUser) {
        LoginUser existingUser = loginUserRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        if (!passwordEncoder.matches(loginUser.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Nieprawidłowe hasło!");
        }

        return jwtUtil.generateToken(existingUser.getUsername(), List.of(existingUser.getRole().name()));
    }

    public LoginUserDTO converLoginUserToDTO(LoginUser loginUser) {
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername(loginUser.getUsername());
        loginUserDTO.setPassword(loginUser.getPassword());
        loginUserDTO.setLibraryUser(loginUser.getLibraryUser());
        return loginUserDTO;
    }

    public LoginUserDTO getLoginUserIdByUsername(String username) {
        return converLoginUserToDTO(loginUserRepository.findByUsername(username).orElseThrow());
    }


}