package com.example.jasionowicz.Book;

import com.example.jasionowicz.BorrowHistory.BorrowHistoryService;
import com.example.jasionowicz.Config.LoginBase.LoginUserDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserService;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserDTO;
import com.example.jasionowicz.User.LibraryUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookService {


    private final BookRepository bookRepository;
    @Autowired
    private BorrowHistoryService borrowHistoryService;
    private final LoginUserService loginUserService;

    public BookService(BookRepository bookRepository, LoginUserService loginUserService) {
        this.bookRepository = bookRepository;
        this.loginUserService = loginUserService;
    }

    @Transactional
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public Book getBook(Integer id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void borrowBook(Integer bookId, UserDetails userDetails) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUserDTO = loginUserService.getLoginUserIdByUsername(username);
        LibraryUser libraryUser = loginUserDTO.getLibraryUser();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Książka jest już wypożyczona!");
        }

        book.setIsAvailable(false);
        book.setLibraryUser(libraryUser);
        book.setBorrowedByUserId(libraryUser.getId());
        bookRepository.save(book);
    }


    @Transactional
    public boolean returnBook(Integer bookId, UserDetails userDetails) {
        String username = userDetails.getUsername();
        Book book = getBook(bookId);
        LoginUserDTO loginUserDTO = loginUserService.getLoginUserIdByUsername(username);

        if (book.isAvailable()) {
            throw new RuntimeException("Ta książka nie jest wypożyczona!");
        }

        if (loginUserDTO == null || loginUserDTO.getLibraryUser() == null) {
            throw new RuntimeException("Użytkownik nie został znaleziony lub nie ma powiązanego LibraryUser");
        }
        Integer userId = book.getBorrowedByUserId();
        if (userId == null) {
            throw new RuntimeException("Brak informacji o użytkowniku, który wypożyczył książkę!");
        }

        if (!Objects.equals(loginUserDTO.getLibraryUser().getId(), userId)) {
            throw new RuntimeException("Nie masz uprawnień do zwrotu tej książki!");
        }
        book.setIsAvailable(true);
        book.setBorrowedByUserId(null);
        borrowHistoryService.stopRecordBorrow(bookId, userId);
        bookRepository.save(book);

        return true;
    }

    public BookDTO convertBookToBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setBorrowCount(book.getBorrowCount());
        bookDTO.setBorrowedByUserId(book.getBorrowedByUserId());
        bookDTO.setIsAvailable(book.getIsAvailable());
        bookDTO.setLibraryUser(book.getLibraryUser());
        return bookDTO;
    }

    public Book convertBookDTOToBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setBorrowCount(bookDTO.getBorrowCount());
        book.setBorrowedByUserId(bookDTO.getBorrowedByUserId());
        book.setIsAvailable(bookDTO.getIsAvailable());
        book.setLibraryUser(bookDTO.getLibraryUser());
        return book;
    }

    public List<BookDTO> getMostBorrowedBooks() {
        return bookRepository.findAll().stream().sorted(Comparator.comparingInt(Book::getBorrowCount).reversed()).map(this::convertBookToBookDTO).collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByUserId(Integer id) {
        List<Book> books = bookRepository.findAllByBorrowedByUserId(id);

        List<BookDTO> borrowedBooks = new ArrayList<>();
        for (Book book : books) {
            BookDTO bookDTO = convertBookToBookDTO(book);
            borrowedBooks.add(bookDTO);
        }
        return borrowedBooks;
    }

    public List<BookDTO> getAllByAuthor(String author) {
        List<Book> allBooksByAuthor = bookRepository.findByAuthorContainingIgnoreCase(author);
        List<BookDTO> borrowedBooks = new ArrayList<>();
        for (Book book : allBooksByAuthor) {
            BookDTO bookDTO = convertBookToBookDTO(book);
            borrowedBooks.add(bookDTO);
        }
        return borrowedBooks;
    }


    public List<BookDTO> getAllByTitle(String title) {
        List<BookDTO> books = bookRepository.findByAuthorContainingIgnoreCase(title)
                .stream()
                .map(this::convertBookToBookDTO)
                .collect(Collectors.toList());
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with title: " + title);
        }
        return books;

    }
}
