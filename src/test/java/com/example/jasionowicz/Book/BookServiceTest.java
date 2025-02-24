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
        Book book = new Book(1, "Effective Java", "Joshua Bloch", true, null,1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBook(1);
        assertNotNull(foundBook);
        assertEquals("Effective Java", foundBook.getTitle());
    }

    @Test
    void shouldDeleteBookById() {
        bookService.deleteBook(1);
    }

    @Test
    void shouldReturnAllBooks() {
        bookService.getAllBooks();
    }

    @Test
    void shouldReturnNullOrError() {
        assertThrows(RuntimeException.class, () -> bookService.getBook(1999999));
    }

    @Test
    void shouldNotBorrowBookIfAlreadyBorrowed() {
        Book book = new Book(1, "Spring Boot", "Craig Walls", false, 2,1);
        LibraryUser user = new LibraryUser(1, "John Doe", "john@example.com");

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(libraryUserRepository.findById(1)).thenReturn(Optional.of(user));

        boolean result = bookService.borrowBook(1, 1);

        assertFalse(result);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void checkIfBookConvertsToBookDTO() {
        BookService bookService = new BookService(null, null);

        Book book = new Book(1, "Spring Boot", "Craig Walls", false, 2,1);
        BookDTO expectedDTO = new BookDTO(1, "Spring Boot", "Craig Walls", false, 2,1);

        BookDTO result = bookService.convertBookToBookDTO(book);

        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getTitle(), result.getTitle());
        assertEquals(expectedDTO.getAuthor(), result.getAuthor());
        assertEquals(expectedDTO.getIsAvailable(), result.getIsAvailable());
        assertEquals(expectedDTO.getBorrowedByUserId(), result.getBorrowedByUserId());
    }

    @Test
    void checkIfBookDTOConvertsToBook() {
        BookService bookService = new BookService(null, null);
        BookDTO bookDTO = new BookDTO(1, "Spring Boot", "Craig Walls", false, 2,1);
        Book expectedBook = new Book(1, "Spring Boot", "Craig Walls", false, 2,1);

        Book result = bookService.convertBookDTOToBook(bookDTO);

        assertEquals(expectedBook.getId(), result.getId());
        assertEquals(expectedBook.getTitle(), result.getTitle());
        assertEquals(expectedBook.getAuthor(), result.getAuthor());
        assertEquals(expectedBook.getIsAvailable(), result.getIsAvailable());
        assertEquals(expectedBook.getBorrowedByUserId(), result.getBorrowedByUserId());
    }

}



