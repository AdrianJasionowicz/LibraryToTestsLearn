package com.example.jasionowicz.Cart;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.Book.BookRepository;
import com.example.jasionowicz.Book.BookService;
import com.example.jasionowicz.BorrowHistory.BorrowHistoryService;
import com.example.jasionowicz.Config.LoginBase.LoginUserDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserService;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserRepository;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BooksCartService {
    @Autowired
    private final BooksCartRepository booksCartRepository;
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final LoginUserService loginUserService;
    @Autowired
    private BorrowHistoryService borrowHistoryService;
    @Autowired
    private LibraryUserRepository libraryUserRepository;
    @Autowired
    private BookService bookService;

    public BooksCartService(BooksCartRepository booksCartRepository, BookRepository bookRepository, LoginUserService loginUserService) {
        this.booksCartRepository = booksCartRepository;
        this.bookRepository = bookRepository;
        this.loginUserService = loginUserService;
    }


    public BooksCart getCartByUser(LibraryUser libraryUser) {
        return booksCartRepository.findByLibraryUser(libraryUser).orElseGet(() -> {
            BooksCart booksCart = new BooksCart();
            booksCart.setLibraryUser(libraryUser);
            booksCart.setBooks(new ArrayList<>());
            BooksCart savedCart = booksCartRepository.save(booksCart);
            return savedCart;
        });
    }


    @Transactional
    public void addBookToCart(UserDetails userDetails, Integer bookId) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUser = loginUserService.getLoginUserIdByUsername(username);
        LibraryUser libraryUser = loginUser.getLibraryUser();

        if (libraryUser == null) {
            throw new RuntimeException("libraryUser == null dla użytkownika " + username);
        }
        BooksCart booksCart = getCartByUser(libraryUser);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki"));

        if (!book.getIsAvailable()) {
            throw new RuntimeException("Książka jest już wypożyczona!");
        }

        if (!booksCart.getBooks().contains(book)) {
            booksCart.getBooks().add(book);
            booksCartRepository.save(booksCart);
        }
    }


    public void removeFromCart(String username, Integer bookId) {
        LibraryUser user = loginUserService.getLoginUserIdByUsername(username).getLibraryUser();
        booksCartRepository.findByLibraryUser(user).ifPresent(cart -> {
            cart.getBooks().removeIf(book -> book.getId().equals(bookId));
            booksCartRepository.save(cart);
        });
    }

    public List<Book> getCart(String username) {
        LibraryUser user = loginUserService.getLoginUserIdByUsername(username).getLibraryUser();
        return booksCartRepository.findByLibraryUser(user)
                .map(BooksCart::getBooks)
                .orElse(Collections.emptyList());
    }

    public void addToCart(String username, Integer bookId) {
        LibraryUser user = loginUserService.getLoginUserIdByUsername(username).getLibraryUser();
        BooksCart cart = booksCartRepository.findByLibraryUser(user).orElse(new BooksCart(null, user, new ArrayList<>()));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Książka nie istnieje!"));
        if (!book.getIsAvailable()) throw new RuntimeException("Książka jest wypożyczona!");

        cart.getBooks().add(book);
        booksCartRepository.save(cart);
    }

    @Transactional
    public void placeOrder(UserDetails userDetails) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUserDTO = loginUserService.getLoginUserIdByUsername(username);
        LibraryUser user = loginUserDTO.getLibraryUser();

        if (user == null) {
            throw new RuntimeException("Brak powiązanego LibraryUser dla użytkownika: " + username);
        }

        BooksCart booksCart = booksCartRepository.findByLibraryUser(user)
                .orElseThrow(() -> new RuntimeException("Brak koszyka dla użytkownika: " + username));

        List<Book> books = new ArrayList<>(booksCart.getBooks());

        if (books.isEmpty()) {
            throw new RuntimeException("Koszyk jest pusty!");
        }

        for (Book book : books) {
            if (!book.getIsAvailable()) {
                throw new RuntimeException("Książka '" + book.getTitle() + "' jest już wypożyczona!");
            }

            borrowHistoryService.recordNewBorrow(book.getId(), user.getId());
            bookService.borrowBook(book.getId(), userDetails);

            book.getCarts().remove(booksCart);
        }

        booksCart.getBooks().clear();
        booksCartRepository.save(booksCart);
    }


}
