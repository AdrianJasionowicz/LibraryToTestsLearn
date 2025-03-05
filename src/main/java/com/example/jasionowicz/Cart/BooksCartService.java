package com.example.jasionowicz.Cart;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.Book.BookRepository;
import com.example.jasionowicz.Config.LoginBase.LoginUser;
import com.example.jasionowicz.Config.LoginBase.LoginUserDTO;
import com.example.jasionowicz.Config.LoginBase.LoginUserService;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserDTO;
import com.example.jasionowicz.User.LibraryUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BooksCartService {
    @Autowired
    private final BooksCartRepository booksCartRepository;
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final LoginUserService loginUserService;

    public BooksCartService(BooksCartRepository booksCartRepository, BookRepository bookRepository, LoginUserService loginUserService) {
        this.booksCartRepository = booksCartRepository;
        this.bookRepository = bookRepository;
        this.loginUserService = loginUserService;
    }


    public BooksCart getCartByUser(LibraryUser libraryUser) {
        return booksCartRepository.findByLibraryUser(libraryUser).orElseGet(() -> {
            BooksCart booksCart = new BooksCart();
            booksCart.setLibraryUser(libraryUser);
            booksCart.setBooks(List.of());
            return booksCartRepository.save(booksCart);
        });
    }

    @Transactional
    public void addBookToCart(UserDetails userDetails, Integer bookId) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUser = loginUserService.getLoginUserIdByUsername(username);
        LibraryUser libraryUser = loginUser.getLibraryUser();


        BooksCart booksCart = getCartByUser(libraryUser);
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Nie znaleziono książki"));

        if (!booksCart.getBooks().contains(book)) {
            booksCart.getBooks().add(book);
            booksCartRepository.save(booksCart);
        }
    }

    @Transactional
    public void removeBookFromCart(UserDetails userDetails, Integer bookId) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUser = loginUserService.getLoginUserIdByUsername(username);
        LibraryUser libraryUser = loginUser.getLibraryUser();


        BooksCart booksCart = getCartByUser(libraryUser);
        booksCart.getBooks().removeIf(book -> book.getId().equals(bookId));
        booksCartRepository.save(booksCart);
    }

    public List<Book> getBooksInCart(UserDetails userDetails) {
        String username = userDetails.getUsername();
        LoginUserDTO loginUser = loginUserService.getLoginUserIdByUsername(username);

        LibraryUser libraryUser = loginUser.getLibraryUser();

        return getCartByUser(libraryUser).getBooks();
    }

    public void processOrder(String username, List<Integer> bookIds) {
        LoginUserDTO loginUser = loginUserService.getLoginUserIdByUsername(username);
        if (loginUser == null) {
            throw new RuntimeException("Użytkownik nie znaleziony!");
        }

        LibraryUser libraryUser = loginUser.getLibraryUser();
        if (libraryUser == null) {
            throw new RuntimeException("Biblioteka użytkownika nie istnieje!");
        }

        BooksCart cart = booksCartRepository.findByLibraryUser(libraryUser)
                .orElse(new BooksCart(null, libraryUser, new ArrayList<>()));

        List<Book> books = bookRepository.findAllById(bookIds);
        if (books.isEmpty()) {
            throw new RuntimeException("Nie znaleziono żadnych książek do zamówienia!");
        }

        cart.setBooks(books);
        booksCartRepository.save(cart);
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
        if (!book.isAvailable()) throw new RuntimeException("Książka jest wypożyczona!");

        cart.getBooks().add(book);
        booksCartRepository.save(cart);
    }

    public void removeFromCart(String username, Integer bookId) {
        LibraryUser user = loginUserService.getLoginUserIdByUsername(username).getLibraryUser();
        booksCartRepository.findByLibraryUser(user).ifPresent(cart -> {
            cart.getBooks().removeIf(book -> book.getId().equals(bookId));
            booksCartRepository.save(cart);
        });
    }

    public void placeOrder(String username) {
        LibraryUser user = loginUserService.getLoginUserIdByUsername(username).getLibraryUser();
        booksCartRepository.deleteByLibraryUser(user);
    }
}
