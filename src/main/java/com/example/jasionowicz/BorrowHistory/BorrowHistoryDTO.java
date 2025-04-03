package com.example.jasionowicz.BorrowHistory;

import com.example.jasionowicz.Book.Book;
import com.example.jasionowicz.Book.BookDTO;
import com.example.jasionowicz.User.LibraryUser;
import com.example.jasionowicz.User.LibraryUserDTO;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BorrowHistoryDTO {

    private Integer id;
    private LibraryUserDTO user;
    private BookDTO book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
