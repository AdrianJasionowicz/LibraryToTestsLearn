package com.example.jasionowicz.Book;

import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LibraryUserRepository libraryUserRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnBookById() {
        Book book = new Book(1L, "Effective Java", "Joshua Bloch", true, null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBook(1L);
        assertNotNull(foundBook);
        assertEquals("Effective Java", foundBook.getTitle());
    }

    @Test
    void shouldDeleteBookById() {
        bookService.deleteBook(1L);
    }

    @Test
    void shouldReturnAllBooks() {
        bookService.getAllBooks();
    }

    @Test
    void shouldReturnNullOrError() {
        assertThrows(RuntimeException.class, () -> bookService.getBook(1999999L));
    }

    @Test
    void shouldNotBorrowBookIfAlreadyBorrowed() {
        Book book = new Book(1L, "Spring Boot", "Craig Walls", false, 2L);
        LibraryUser user = new LibraryUser(1L, "John Doe", "john@example.com");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(libraryUserRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = bookService.borrowBook(1L, 1L);

        assertFalse(result);
        verify(bookRepository, never()).save(any());
    }
}



