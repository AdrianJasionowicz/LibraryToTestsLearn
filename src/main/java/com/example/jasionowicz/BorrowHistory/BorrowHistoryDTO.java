package com.example.jasionowicz.BorrowHistory;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.User.LibraryUser;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BorrowHistoryDTO {

    private Integer id;
    private LibraryUser user;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
