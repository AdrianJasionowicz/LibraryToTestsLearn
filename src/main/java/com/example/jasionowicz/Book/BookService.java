package com.example.jasionowicz.Book;

import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {


    private BookRepository bookRepository;
    private LibraryUserRepository libraryUserRepository;

    public BookService(BookRepository bookRepository, LibraryUserRepository libraryUserRepository) {
        this.bookRepository = bookRepository;
        this.libraryUserRepository = libraryUserRepository;
    }

    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public Book getBook(long id) {
        return bookRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Book not found"));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public boolean borrowBook(long bookId, long userId) {
        Book book = getBook(bookId);
        LibraryUser user = libraryUserRepository.getReferenceById(userId);
        if (book != null && user != null && book.isAvailable()) {
            book.setIsAvailable(false);
            book.setBorrowedByUserId(userId);
            bookRepository.save(book);
            return true;
        }
        return false;
    }

public BookDTO convertBookToBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setBorrowedByUserId(book.getBorrowedByUserId());
        bookDTO.setIsAvailable(book.getIsAvailable());
        return bookDTO;
}

public Book convertBookDTOToBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setBorrowedByUserId(bookDTO.getBorrowedByUserId());
        book.setIsAvailable(bookDTO.getIsAvailable());
        return book;
}

}
