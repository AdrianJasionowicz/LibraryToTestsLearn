package com.example.jasionowicz.Book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(bookService);
    }

    @Test
    void getAllBooks() throws Exception {
        List<BookDTO> books = Arrays.asList(
                new BookDTO(1L, "Clean Code", "Robert C. Martin", true, null),
                new BookDTO(2L, "Effective Java", "Joshua Bloch", true, null)
        );

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void getBook() throws Exception {
        BookDTO book = new BookDTO(1L, "Refactoring", "Martin Fowler", true, null);

        when(bookService.getBook(1L)).thenReturn(book);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Refactoring"));
    }

    @Test
    void addBook() throws Exception {
        BookDTO book = new BookDTO(null, "Design Patterns", "GOF", true, null);

        when(bookService.addBook(Mockito.any())).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());
    }

    @Test
    void updateBook() throws Exception {
        BookDTO book = new BookDTO(1L, "Updated Book", "Updated Author", true, null);

        when(bookService.updateBook(Mockito.any())).thenReturn(book);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void deleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void borrowBook() throws Exception {
        when(bookService.borrowBook(1L, 2L)).thenReturn(true);

        mockMvc.perform(put("/books/1/borrow/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book borrowed successfully"));
    }
}
