package com.example.jasionowicz.Book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getAllBooks() throws Exception {
        List<Book> books = List.of(
                new Book(1, "Clean Code", "Robert C. Martin", true, null,1),
                new Book(2, "Effective Java", "Joshua Bloch", true, null,1)
        );

        when(bookService.getAllBooks()).thenReturn(books);
        when(bookService.convertBookToBookDTO(any())).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.isAvailable(), book.getBorrowedByUserId(),book.getBorrowCount());
        });

        List<BookDTO> bookDTOs = books.stream()
                .map(bookService::convertBookToBookDTO)
                .collect(Collectors.toList());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bookDTOs.size()))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void getBook() throws Exception {
        Book book = new Book(1, "Refactoring", "Martin Fowler", true, 0,1);
        when(bookService.getBook(1)).thenReturn(book);
        when(bookService.convertBookToBookDTO(book)).thenReturn(new BookDTO(1, "Refactoring", "Martin Fowler", true, 0,1));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Refactoring"));
    }

    @Test
    void addBook() throws Exception {
        BookDTO bookDTO = new BookDTO(null, "Design Patterns", "GOF", true, 0,1);
        Book book = new Book(1, "Design Patterns", "GOF", true, 0,1);

        when(bookService.convertBookDTOToBook(any())).thenReturn(book);

        doNothing().when(bookService).addBook(any());

        when(bookService.convertBookToBookDTO(book)).thenReturn(bookDTO);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isOk());
    }


    @Test
    void updateBook() throws Exception {
        BookDTO bookDTO = new BookDTO(1, "Updated Book", "Updated Author", true, 0,1);
        Book book = new Book(1, "Updated Book", "Updated Author", true, 0,1);

        when(bookService.convertBookDTOToBook(any())).thenReturn(book);
        doNothing().when(bookService).updateBook(book);
        when(bookService.convertBookToBookDTO(book)).thenReturn(bookDTO);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void deleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void borrowBook() throws Exception {
        when(bookService.borrowBook(1, 2)).thenReturn(true);

        mockMvc.perform(put("/books/1/borrow/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book borrowed successfully"));
    }

    @Test
    void shouldReturnBookSuccessfully() {
        Book book = new Book(1, "Spring in Action", "Craig Walls", false, 2,1);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        boolean result = bookService.returnBook(1);

        assertTrue(result);
        assertTrue(book.isAvailable());
        assertNull(book.getBorrowedByUserId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void shouldThrowExceptionIfBookNotBorrowed() {
        Book book = new Book(1, "Spring Boot", "Josh Long", true, null,1);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Exception exception = assertThrows(RuntimeException.class, () -> bookService.returnBook(1));

        assertEquals("Book with id 1 is not borrowed.", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }


}
