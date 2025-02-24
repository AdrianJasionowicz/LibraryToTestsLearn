package com.example.jasionowicz.Book;

import com.example.jasionowicz.BorrowHistory.BorrowHistoryService;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {


    private BookRepository bookRepository;
    private LibraryUserRepository libraryUserRepository;
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    public BookService(BookRepository bookRepository, LibraryUserRepository libraryUserRepository) {
        this.bookRepository = bookRepository;
        this.libraryUserRepository = libraryUserRepository;
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
    public boolean borrowBook(Integer bookId, Integer userId) {
        Book book = getBook(bookId);
        LibraryUser user = libraryUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (book != null && user != null && book.isAvailable()) {
            book.setIsAvailable(false);
            book.setBorrowedByUserId(userId);
            book.increaseBorrowCount();
            bookRepository.save(book);
            borrowHistoryService.recordNewBorrow(bookId, userId);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean returnBook(Integer bookId) {
        Book book = getBook(bookId);
        Integer userId = book.getBorrowedByUserId();

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
        List<Book> allBooksByAuthor = bookRepository.getAllByAuthor(author);
        List<BookDTO> borrowedBooks = new ArrayList<>();
        for (Book book : allBooksByAuthor) {
            BookDTO bookDTO = convertBookToBookDTO(book);
            borrowedBooks.add(bookDTO);
        }
        return borrowedBooks;
    }

    public List<BookDTO> getAllByTitle(String title) {
        List<Book> allBooksByTitle = bookRepository.getAllByTitle(title);
        List<BookDTO> borrowedBooks = new ArrayList<>();
        for (Book book : allBooksByTitle) {
            BookDTO bookDTO = convertBookToBookDTO(book);
            borrowedBooks.add(bookDTO);
        }
        return borrowedBooks;
    }


}
