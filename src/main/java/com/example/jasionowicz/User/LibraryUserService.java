package com.example.jasionowicz.User;

import com.example.jasionowicz.Book.BookDTO;
import com.example.jasionowicz.Book.BookService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryUserService {
    private LibraryUserRepository libraryUserRepository;
    private final BookService bookService;

    public LibraryUserService(LibraryUserRepository libraryUserRepository, BookService bookService) {
        this.libraryUserRepository = libraryUserRepository;
        this.bookService = bookService;
    }

    public LibraryUser save(LibraryUser libraryUser) {
        return libraryUserRepository.save(libraryUser);
    }

    public LibraryUser getLibraryUser(Integer id) {
        return libraryUserRepository.getReferenceById(id);
    }

    public List<LibraryUser> getLibraryUsers() {
        return libraryUserRepository.findAll();
    }

    public void deleteLibraryUser(Integer id) {
        libraryUserRepository.deleteById(id);
    }

    public void updateLibraryUser(Integer id, LibraryUser libraryUser) {
        LibraryUser oldLibraryUser = libraryUserRepository.getReferenceById(id);
        oldLibraryUser.setName(libraryUser.getName());
        oldLibraryUser.setEmail(libraryUser.getEmail());
    }

    public void createLibraryUser(LibraryUser libraryUser) {

        libraryUserRepository.save(libraryUser);
    }


    public LibraryUserDTO getLibraryUserByEmail(String email) {
        LibraryUser libraryUser = libraryUserRepository.findByEmail(email);
        LibraryUserDTO libraryUserDTO = convertLibraryUserToLibraryUserDTO(libraryUser);
        return libraryUserDTO;
    }

    public LibraryUserDTO convertLibraryUserToLibraryUserDTO(LibraryUser libraryUser) {
        LibraryUserDTO libraryUserDTO = new LibraryUserDTO();
        libraryUserDTO.setId(libraryUser.getId());
        libraryUserDTO.setName(libraryUser.getName());
        libraryUserDTO.setEmail(libraryUser.getEmail());
        libraryUserDTO.setBorrowedBooks(libraryUser.getBorrowedBooks());
        libraryUserDTO.setAccountBalance(libraryUser.getAccountBalance());
        return libraryUserDTO;
    }

    public LibraryUser convertLibraryUserDTOToLibraryUser(LibraryUserDTO libraryUserDTO) {
        LibraryUser libraryUser = new LibraryUser();
        libraryUser.setId(libraryUserDTO.getId());
        libraryUser.setName(libraryUserDTO.getName());
        libraryUser.setEmail(libraryUserDTO.getEmail());
        libraryUser.setBorrowedBooks(libraryUserDTO.getBorrowedBooks());
        libraryUser.setAccountBalance(libraryUserDTO.getAccountBalance());
        return libraryUser;
    }

    public List<BookDTO> getBorrowedBooks(Integer userId) {
        return new ArrayList<>(bookService.getBooksByUserId(userId));
    }

    public void setAccountBalance(int id, int accountBalance) {
        LibraryUserDTO libraryUserDTO = convertLibraryUserToLibraryUserDTO(libraryUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id " + id + " not found")));
        libraryUserDTO.setAccountBalance(accountBalance);
        LibraryUser libraryUser = new LibraryUser();
        libraryUser = convertLibraryUserDTOToLibraryUser(libraryUserDTO);
        libraryUserRepository.save(libraryUser);
        ///  WTF XDDDDDDDDDDD
    }


}
