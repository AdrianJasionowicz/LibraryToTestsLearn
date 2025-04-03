package com.example.jasionowicz.Book;

import com.example.jasionowicz.BorrowHistory.BorrowHistoryService;
import com.example.jasionowicz.Cart.BooksCart;
import com.example.jasionowicz.Cart.BooksCartRepository;
import com.example.jasionowicz.Config.LoginBase.LoginUser;
import com.example.jasionowicz.Config.LoginBase.LoginUserDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserRepository;
import com.example.jasionowicz.Config.LoginBase.LoginUserService;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserDTO;
import com.example.jasionowicz.User.LibraryUserRepository;
import com.example.jasionowicz.User.LibraryUserService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tomcat.jni.Library;
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
    private BooksCartRepository booksCartRepository;
    @Autowired
    private LibraryUserRepository libraryUserRepository;
    private LoginUserRepository loginUserRepository;

    public BookService(BookRepository bookRepository, LoginUserService loginUserService, BooksCartRepository booksCartRepository, LoginUserRepository loginUserRepository) {
        this.bookRepository = bookRepository;
        this.loginUserService = loginUserService;
        this.booksCartRepository = booksCartRepository;
        this.loginUserRepository = loginUserRepository;
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

        if (!book.getIsAvailable()) {
            throw new RuntimeException("Książka jest już wypożyczona!");
        }

        book.setIsAvailable(false);
        book.setLibraryUser(libraryUser);
        bookRepository.save(book);
    }


    @Transactional
    public boolean returnBook(Integer bookId, UserDetails userDetails) {
        String username = userDetails.getUsername();
        Book book = getBook(bookId);
        LoginUserDTO loginUserDTO = loginUserService.getLoginUserIdByUsername(username);

        if (book.getIsAvailable()) {
            throw new RuntimeException("Ta książka nie jest wypożyczona!");
        }

        boolean isAdminOrMod = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MODERATOR"));

        LibraryUser userFromDTO = loginUserDTO.getLibraryUser();
        LibraryUser bookOwner = book.getLibraryUser();

        if (!isAdminOrMod && !bookOwner.getId().equals(userFromDTO.getId())) {
            throw new RuntimeException("Nie masz uprawnień do zwrotu tej książki!");
        }

        borrowHistoryService.stopRecordBorrow(bookId, bookOwner.getId());

        if (book.getCarts() != null) {
            for (BooksCart cart : new ArrayList<>(book.getCarts())) {
                cart.getBooks().remove(book);
                booksCartRepository.save(cart);
            }
        }

        book.setIsAvailable(true);
        book.setLibraryUser(null);
        bookRepository.save(book);

        if (userFromDTO.getLoginUser() == null) {
            LoginUser user = loginUserRepository.findByUsername(username).get();
            userFromDTO.setLoginUser(user);
            libraryUserRepository.save(userFromDTO);
        }

        return true;
    }


    public BookDTO convertBookToBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setBorrowCount(book.getBorrowCount());
        bookDTO.setIsAvailable(book.getIsAvailable());
        bookDTO.setLibraryUserDTO(book.getLibraryUser());
        return bookDTO;
    }

    public Book convertBookDTOToBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setBorrowCount(bookDTO.getBorrowCount());
        book.setIsAvailable(bookDTO.getIsAvailable());
        book.setLibraryUser(bookDTO.getLibraryUserDTO());
        return book;
    }

    public List<BookDTO> getMostBorrowedBooks() {
        return bookRepository.findAll().stream().sorted(Comparator.comparingInt(Book::getBorrowCount).reversed()).map(this::convertBookToBookDTO).collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByUserId(Integer id) {
        List<Book> books = bookRepository.findAllByLibraryUser_Id(id);

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
        List<BookDTO> books = bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertBookToBookDTO)
                .collect(Collectors.toList());
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with title: " + title);
        }
        return books;

    }
}
