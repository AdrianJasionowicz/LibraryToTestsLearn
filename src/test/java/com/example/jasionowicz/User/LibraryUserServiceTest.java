package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.Book.BookDTO;
import com.example.jasionowicz.Book.BookRepository;
import com.example.jasionowicz.Book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryUserServiceTest {

    @Mock
    private LibraryUserRepository libraryUserRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private LibraryUserService libraryUserService;
    @Mock
    private BookService bookService;
    private List<BookDTO> borrowedBooks;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        borrowedBooks = new ArrayList<>();
        borrowedBooks.add(new BookDTO(1, "Book One", "Author One", false, 100, 2));
        borrowedBooks.add(new BookDTO(2, "Book Two", "Author Two", false, 100, 3));
    }
    @Test
    void save() {
    }

    @Test
    void getLibraryUser() {
    }

    @Test
    void getLibraryUsers() {
    }

    @Test
    void deleteLibraryUser() {
    }

    @Test
    void updateLibraryUser() {
    }

    @Test
    void createLibraryUser() {
    }

    @Test
    void getLibraryUserByEmail() {
    }

    @Test
    void convertLibraryUserToLibraryUserDTO() {
    }

    @Test
    void convertLibraryUserDTOToLibraryUser() {
    }





}