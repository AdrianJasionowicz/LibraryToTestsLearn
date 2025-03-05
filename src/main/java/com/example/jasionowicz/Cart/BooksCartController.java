package com.example.jasionowicz.Cart;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.User.LibraryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class BooksCartController {
@Autowired
    private final BooksCartService booksCartService;

    public BooksCartController(BooksCartService booksCartService) {
        this.booksCartService = booksCartService;
    }

    @PostMapping("/add/{bookId}")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer bookId) {

        booksCartService.addBookToCart(userDetails, bookId);
        return ResponseEntity.ok("Książka dodana do koszyka!");
    }

    @GetMapping("/get")
    public ResponseEntity<List<Book>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(booksCartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Integer> request) {
        booksCartService.addToCart(userDetails.getUsername(), request.get("bookId"));
        return ResponseEntity.ok("Dodano do koszyka!");
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<String> removeFromCart(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer bookId) {
        booksCartService.removeFromCart(userDetails.getUsername(), bookId);
        return ResponseEntity.ok("Usunięto z koszyka!");
    }

    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        booksCartService.placeOrder(userDetails.getUsername());
        return ResponseEntity.ok("Zamówienie złożone!");
    }
}
