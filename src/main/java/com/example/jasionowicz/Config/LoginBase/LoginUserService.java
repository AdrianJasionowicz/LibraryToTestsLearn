package com.example.jasionowicz.Config.LoginBase;

import com.example.jasionowicz.Config.JwtUtil;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserService;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private LoginUserRepository loginUserRepository;
    private LibraryUserService libraryUserService;


    public LoginUserService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, LoginUserRepository loginUserRepository, LibraryUserService libraryUserService) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginUserRepository = loginUserRepository;
        this.libraryUserService = libraryUserService;
    }

    public ResponseEntity<?> makeNewLoginUser(LoginUserDTO loginUserDTO) {
        if (loginUserRepository.findByUsername(loginUserDTO.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Użytkownik o tej nazwie już istnieje!");
        }

        LoginUser loginUser2 = new LoginUser();
        loginUser2.setPassword(passwordEncoder.encode(loginUserDTO.getPassword()));
        loginUser2.setUsername(loginUserDTO.getUsername());

        loginUserRepository.save(loginUser2);

        LibraryUser libraryUser = new LibraryUser();
        libraryUser.setName("None");
        libraryUser.setEmail("None");
        libraryUser.setAccountBalance(0);
        libraryUser.setLoginUser(loginUser2);

        loginUser2.setLibraryUser(libraryUser);

        libraryUserService.save(libraryUser);


        return ResponseEntity.ok().body("User has been made");
    }


    public String authenticateUser(LoginUser loginUser) {
        LoginUser existingUser = loginUserRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

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