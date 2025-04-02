    package com.example.jasionowicz.Book;

    import com.example.jasionowicz.Config.LoginBase.LoginUser;
    import com.example.jasionowicz.Config.LoginBase.LoginUserRepository;
    import com.example.jasionowicz.User.LibraryUser;
    import com.example.jasionowicz.User.LibraryUserRepository;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
    public class BookController {

        private final BookService bookService;
        private final LoginUserRepository loginUserRepository;
        private final LibraryUserRepository libraryUserRepository;

        public BookController(BookService bookService, LoginUserRepository loginUserRepository, LibraryUserRepository libraryUserRepository) {
            this.bookService = bookService;
            this.loginUserRepository = loginUserRepository;
            this.libraryUserRepository = libraryUserRepository;
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

    @PutMapping("/{bookId}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable Integer bookId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji! Użytkownik nierozpoznany.");
        }
        bookService.borrowBook(bookId, userDetails);
        return ResponseEntity.ok("Książka wypożyczona");
    }

    @PutMapping("/moderator/return/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<String> returnBook(@PathVariable Integer id,@AuthenticationPrincipal UserDetails userDetails) {
        try {
            boolean success = bookService.returnBook(id, userDetails);
            if (success) {
                return ResponseEntity.ok("Książka została zwrócona!");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nie masz uprawnień do zwrotu tej książki!");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        }

        @GetMapping("/stats")
        @ResponseBody
        public List<BookDTO> getMostBorrowedBooks() {
            return bookService.getMostBorrowedBooks();
        }


        @GetMapping("/search/getByAuthor/{author}")
        public ResponseEntity<List<BookDTO>> getAllBooksByAuthor(@PathVariable String author) {
            return ResponseEntity.ok().body(bookService.getAllByAuthor(author));
        }

        @GetMapping("/search/getByTitle/{bookName}")
        @ResponseBody
        public ResponseEntity<List<BookDTO>> getAllBooksByName(@PathVariable String bookName) {
            return ResponseEntity.ok().body(bookService.getAllByTitle(bookName));
        }




    }
