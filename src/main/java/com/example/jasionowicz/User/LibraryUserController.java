package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.BookDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserRole;
import com.example.jasionowicz.Config.LoginBase.LoginUserViewForAdmin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PutMapping("/user/changeEmail")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody String email, @RequestParam String password) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Brak autoryzacji");
        }
        libraryUserService.updateEmail(userDetails,email,password);

        return ResponseEntity.ok().body("Zmieniono Dane");
    }

    @PutMapping("/user/changeName")
    public ResponseEntity<?> updateName(@AuthenticationPrincipal UserDetails userDetails, @RequestBody String name, @RequestParam String password) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Brak autoryzacji");
        }
        return libraryUserService.updateNameOfUser(userDetails, name, password);
    }

    @PutMapping("/user/changePassword")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody String oldPassword, @RequestParam String newPassword) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Brak autoryzacji");
        }
        libraryUserService.updatePassword(userDetails,oldPassword,newPassword);

        return ResponseEntity.ok().body("Zmieniono Dane");
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

    @GetMapping("/user/getUserInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Brak uzytkownika");
        }
       return ResponseEntity.ok(libraryUserService.getProfile(userDetails));
    }

        @GetMapping("/user/getAllLibraryUsers")
      @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERATOR')")
        public ResponseEntity<List<LoginUserViewForAdmin>> getAllLibraryUsers() {

        return libraryUserService.getAllLibraryUsers();
        }



    @PatchMapping("/user/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setAuthority(@PathVariable Integer id,
                                          @RequestBody LoginUserRole loginUserRole) {
        return libraryUserService.setUserAuthorityById(id, loginUserRole);
    }
}
