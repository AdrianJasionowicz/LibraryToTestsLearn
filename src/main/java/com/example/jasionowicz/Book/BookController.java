package com.example.jasionowicz.Book;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/getAll")
    @ResponseBody
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks()
                .stream()
                .map(bookService::convertBookToBookDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Integer id) {
        try {
            Book book = bookService.getBook(id);
            return ResponseEntity.ok(bookService.convertBookToBookDTO(book));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookService.convertBookDTOToBook(bookDTO);
        bookService.addBook(book);
        return ResponseEntity.ok(bookService.convertBookToBookDTO(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Integer id, @RequestBody BookDTO bookDTO) {
        Book book = bookService.convertBookDTOToBook(bookDTO);
        bookService.updateBook(book);
        return ResponseEntity.ok(bookService.convertBookToBookDTO(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/borrow/{userId}")
    public ResponseEntity<String> borrowBook(@PathVariable Integer id, @PathVariable Integer userId) {
        boolean success = bookService.borrowBook(id, userId);
        if (success) {
            return ResponseEntity.ok("Book borrowed successfully");
        } else {
            return ResponseEntity.badRequest().body("Book is unavailable or user not found");
        }
    }
    @PutMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Integer id) {
        try {
            bookService.returnBook(id);
            return ResponseEntity.ok("Book returned successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stats")
    @ResponseBody
    public List<BookDTO> getMostBorrowedBooks() {
        return bookService.getMostBorrowedBooks();
    }


    @GetMapping("/{author}/getAll")
    public ResponseEntity<List<BookDTO>> getAllBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok().body(bookService.getAllByAuthor(author));
    }

    @GetMapping("/{bookName}/getByTitle")
    @ResponseBody
    public ResponseEntity<List<BookDTO>> getAllBooksByName(@PathVariable String bookName) {
        return ResponseEntity.ok().body(bookService.getAllByTitle(bookName));
    }

}
