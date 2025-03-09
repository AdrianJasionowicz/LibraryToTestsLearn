package com.example.jasionowicz.BorrowHistory;

import com.example.jasionowicz.Book.BookService;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserDTO;
import com.example.jasionowicz.User.LibraryUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BorrowHistoryService {

    private final BorrowHistoryRepository borrowHistoryRepository;
    private final LibraryUserService libraryUserService;
    private final BookService bookService;


    public BorrowHistoryService(BorrowHistoryRepository borrowHistoryRepository, LibraryUserService libraryUserService, BookService bookService) {
        this.borrowHistoryRepository = borrowHistoryRepository;
        this.libraryUserService = libraryUserService;
        this.bookService = bookService;
    }


    public ResponseEntity recordNewBorrow(int bookId, int userId) {

        if (userId == 0) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }

        if (bookId == 0) {
            return ResponseEntity.badRequest().body("Book is not available");
        }

        BorrowHistory borrowHistory = new BorrowHistory();
        borrowHistory.setBorrowDate(LocalDate.now());
        borrowHistory.setUser(libraryUserService.getLibraryUser(userId));
        borrowHistory.setBook(bookService.getBook(bookId));
        borrowHistoryRepository.save(borrowHistory);

        return ResponseEntity.ok().body("Success");
    }


    public void stopRecordBorrow(Integer bookId, int userId) {
        List<BorrowHistory> borrowedByUserHistory = borrowHistoryRepository.findAllByUserId(userId);
        for (BorrowHistory borrowHistory : borrowedByUserHistory) {
            if (borrowHistory.getBook().getId().equals(bookId) && borrowHistory.getReturnDate() == null) {
                borrowHistory.setReturnDate(LocalDate.now());
                borrowHistoryRepository.save(borrowHistory);
                shouldChargeUser(borrowHistory);
                break;
            }
        }
    }

    public void shouldChargeUser(BorrowHistory borrowHistory) {
        int userId = borrowHistory.getUser().getId();
        int ammount = checkIfBorrowedBookWasHeldToLong(borrowHistory);
        if (ammount < 0) {
           LibraryUserDTO libraryUserDTO =  libraryUserService.convertLibraryUserToLibraryUserDTO(libraryUserService.getLibraryUser(userId));
            libraryUserDTO.setAccountBalance(ammount);
            libraryUserService.save(libraryUserService.convertLibraryUserDTOToLibraryUser(libraryUserDTO));
        }
    }


    public int checkIfBorrowedBookWasHeldToLong(BorrowHistory borrowHistory) {
       LocalDate borrowDate = borrowHistory.getBorrowDate();
      LocalDate returnDate =  borrowHistory.getReturnDate();
      if (borrowDate == null || returnDate == null) {
          throw new IllegalArgumentException("Borrow Date and Return Date are mandatory");
      }

      int maxBorrowDays = 28;
      int penaltyPerDay = -2;
        long daysBetween = ChronoUnit.DAYS.between(borrowDate, returnDate);

        if ( daysBetween > maxBorrowDays ) {
            long overDueDays = daysBetween - maxBorrowDays;
            return (int) overDueDays * penaltyPerDay;
        }
        return 0;
    }
    

    public List<BorrowHistoryDTO> getAllLibraryUserBorrowedHistory(int userId) {
        List<BorrowHistory> borrowedHistory = borrowHistoryRepository.findAllByUserId(userId);
        System.out.println(borrowedHistory);
        System.out.println(borrowHistoryRepository.findAllByUserId(userId));
        System.out.println(borrowHistoryRepository.findAll());
        List<BorrowHistoryDTO> borrowHistoryDTOList = new ArrayList<>();
        for (BorrowHistory borrowHistory : borrowedHistory) {
           BorrowHistoryDTO borrowHistoryDTO = convertBorrowHistoryToDTO(borrowHistory);
            borrowHistoryDTOList.add(borrowHistoryDTO);
        }
        return borrowHistoryDTOList;
    }

    public BorrowHistoryDTO convertBorrowHistoryToDTO(BorrowHistory borrowHistory) {
        BorrowHistoryDTO borrowHistoryDTO = new BorrowHistoryDTO();
        borrowHistoryDTO.setId(borrowHistory.getId());
        borrowHistoryDTO.setReturnDate(borrowHistory.getReturnDate());
        borrowHistoryDTO.setBorrowDate(borrowHistory.getBorrowDate());
        borrowHistoryDTO.setUser(borrowHistory.getUser());
        borrowHistoryDTO.setBook(borrowHistory.getBook());
        return borrowHistoryDTO;
    }



}
