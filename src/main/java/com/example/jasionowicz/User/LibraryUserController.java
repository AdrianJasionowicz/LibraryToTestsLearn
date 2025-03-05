package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LibraryUserController {

private final LibraryUserService libraryUserService;

    public LibraryUserController(LibraryUserService libraryUserService) {
        this.libraryUserService = libraryUserService;
    }


    @GetMapping("/{id}/borrowed-books")
    public ResponseEntity<List<BookDTO>> getBorrowedBooks(@PathVariable Integer id) {
        List<BookDTO> borrowedBooks = libraryUserService.getBorrowedBooks(id);
        return ResponseEntity.ok(borrowedBooks);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String password) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
        libraryUserService.deleteLibraryUser(userDetails,password);

        return ResponseEntity.ok("Konto zostało usunięte!");
    }

    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDetails userDetails, LibraryUserDTO libraryUserDto) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Brak autoryzacji");
        }
        if (libraryUserDto == null) {
            return ResponseEntity.badRequest().body("Brak danych do zmienienia!!!");
        }
        libraryUserService.updateLibraryUser(userDetails,libraryUserDto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/transferCashToAccount")
    public ResponseEntity<String> transferUpYourAccount(@AuthenticationPrincipal UserDetails userDetails, Integer ammount) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Brak autoryzacji");
        }
        if (ammount < 0) {
            return ResponseEntity.badRequest().body("Brak zasilenia");
        }
        libraryUserService.setAccountBalance(userDetails,ammount);
        return ResponseEntity.ok().build();
    }
}
