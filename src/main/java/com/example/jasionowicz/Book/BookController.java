package com.example.jasionowicz.Book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks()
                .stream()
                .map(bookService::convertBookToBookDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable long id) {
        try {
            Book book = bookService.getBook(id);
            return ResponseEntity.ok(bookService.convertBookToBookDTO(book));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookService.convertBookDTOToBook(bookDTO);
        bookService.addBook(book);
        return ResponseEntity.ok(bookService.convertBookToBookDTO(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable long id, @RequestBody BookDTO bookDTO) {
        Book book = bookService.convertBookDTOToBook(bookDTO);
        bookService.updateBook(book);
        return ResponseEntity.ok(bookService.convertBookToBookDTO(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/borrow/{userId}")
    public ResponseEntity<String> borrowBook(@PathVariable long id, @PathVariable long userId) {
        boolean success = bookService.borrowBook(id, userId);
        if (success) {
            return ResponseEntity.ok("Book borrowed successfully");
        } else {
            return ResponseEntity.badRequest().body("Book is unavailable or user not found");
        }
    }
}
