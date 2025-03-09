package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.BookDTO;
import com.example.jasionowicz.Book.BookService;
import com.example.jasionowicz.Config.LoginBase.LoginUser;
import com.example.jasionowicz.Config.LoginBase.LoginUserDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserRepository;
import com.example.jasionowicz.Config.LoginBase.LoginUserService;
import jakarta.validation.constraints.Null;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryUserService {
    private final LoginUserRepository loginUserRepository;
    private final LibraryUserRepository libraryUserRepository;
    private final BookService bookService;
    private final LoginUserService loginUserService;
    private PasswordEncoder passwordEncoder;

    public LibraryUserService(LibraryUserRepository libraryUserRepository, BookService bookService, LoginUserRepository loginUserRepository, PasswordEncoder passwordEncoder, LoginUserService loginUserService) {
        this.libraryUserRepository = libraryUserRepository;
        this.bookService = bookService;
        this.loginUserRepository = loginUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginUserService = loginUserService;
    }

    public void save(LibraryUser libraryUser) {
         libraryUserRepository.save(libraryUser);
    }

    public LibraryUser getLibraryUser(Integer id) {
        return libraryUserRepository.getReferenceById(id);
    }

    public LibraryUserDTO getLibraryUserByEmail(String email) {
        LibraryUser libraryUser = libraryUserRepository.findByEmail(email);
        LibraryUserDTO libraryUserDTO = convertLibraryUserToLibraryUserDTO(libraryUser);
        return libraryUserDTO;
    }

    public LibraryUserDTO convertLibraryUserToLibraryUserDTO(LibraryUser libraryUser) {
        LibraryUserDTO libraryUserDTO = new LibraryUserDTO();
        libraryUserDTO.setId(libraryUser.getId());
        libraryUserDTO.setName(libraryUser.getName());
        libraryUserDTO.setEmail(libraryUser.getEmail());
        libraryUserDTO.setBorrowedBooks(libraryUser.getBorrowedBooks());
        libraryUserDTO.setAccountBalance(libraryUser.getAccountBalance());
        return libraryUserDTO;
    }

    public LibraryUser convertLibraryUserDTOToLibraryUser(LibraryUserDTO libraryUserDTO) {
        LibraryUser libraryUser = new LibraryUser();
        libraryUser.setId(libraryUserDTO.getId());
        libraryUser.setName(libraryUserDTO.getName());
        libraryUser.setEmail(libraryUserDTO.getEmail());
        libraryUser.setBorrowedBooks(libraryUserDTO.getBorrowedBooks());
        libraryUser.setAccountBalance(libraryUserDTO.getAccountBalance());
        return libraryUser;
    }



    public List<BookDTO> getBorrowedBooks(Integer userId) {
        return new ArrayList<>(bookService.getBooksByUserId(userId));
    }

    public ResponseEntity setAccountBalance(UserDetails userDetails, int accountBalance) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUserDTO = loginUserService.getLoginUserIdByUsername(username);
        LibraryUserDTO libraryUserDTO = convertLibraryUserToLibraryUserDTO(loginUserDTO.getLibraryUser());
        libraryUserDTO.setAccountBalance(accountBalance);
        libraryUserRepository.save(convertLibraryUserDTOToLibraryUser(libraryUserDTO));

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity<String> deleteLibraryUser(UserDetails userDetails, String password) {
        String username = userDetails.getUsername();
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nieprawidłowe hasło!");
        }
        LoginUser loginUser = loginUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        if (!passwordEncoder.matches(password, loginUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nieprawidłowe hasło!");
        }
        LibraryUser libraryUser = loginUser.getLibraryUser();
        if (libraryUser == null) {
            return ResponseEntity.badRequest().body("Brak powiązanego konta bibliotecznego!");
        }
        if (libraryUser.getAccountBalance() < 0) {
            return ResponseEntity.badRequest().body("Twoje saldo jest ujemne, nie możesz usunąć konta!");
        }
        libraryUserRepository.delete(libraryUser);
        loginUserRepository.delete(loginUser);
        return ResponseEntity.ok().body("Konto zostało usunięte!");
    }

    public void updateLibraryUser(UserDetails userDetails, LibraryUserDTO libraryUserDTO) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUserDTO = loginUserService.getLoginUserIdByUsername(username);
        LibraryUserDTO oldLibraryUserDTO = convertLibraryUserToLibraryUserDTO(loginUserDTO.getLibraryUser());

        LibraryUser oldLibraryUser = libraryUserRepository.getReferenceById(oldLibraryUserDTO.getId());
        if (libraryUserDTO.getName() != null && !libraryUserDTO.getName().isBlank()) {
            oldLibraryUser.setName(libraryUserDTO.getName());
        }

        if (libraryUserDTO.getEmail() != null && !libraryUserDTO.getEmail().isBlank()) {
            oldLibraryUser.setEmail(libraryUserDTO.getEmail());
        }
       save(convertLibraryUserDTOToLibraryUser(oldLibraryUserDTO));

    }

}
