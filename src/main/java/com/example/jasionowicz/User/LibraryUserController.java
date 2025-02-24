package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.BookDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

}
